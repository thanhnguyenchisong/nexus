package com.pulse.ai.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.ai.model.AiAnalysisResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @ConfigProperty(name = "openai.api.key", defaultValue = "")
    String apiKey;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public AiAnalysisResult analyze(String prompt) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("${OPENAI_API_KEY}")) {
            throw new AiProviderException("OpenAI API key not configured");
        }

        try {
            String requestBody = MAPPER.writeValueAsString(Map.of(
                    "model", MODEL,
                    "max_tokens", 600,
                    "temperature", 0.3,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("OpenAI returned HTTP {}: {}", response.statusCode(), response.body());
                throw new AiProviderException("OpenAI HTTP " + response.statusCode());
            }

            JsonNode root = MAPPER.readTree(response.body());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            return parseAiJson(content, "OPENAI", MODEL);

        } catch (AiProviderException e) {
            throw e;
        } catch (Exception e) {
            log.error("OpenAI request failed: {}", e.getMessage());
            throw new AiProviderException("OpenAI error: " + e.getMessage(), e);
        }
    }

    static AiAnalysisResult parseAiJson(String json, String provider, String model) {
        try {
            // Strip markdown code fences if present
            String cleaned = json.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("^```[a-z]*\\n?", "").replaceAll("```$", "").trim();
            }
            JsonNode node = MAPPER.readTree(cleaned);

            String summary = node.path("summary").asText("").substring(
                    0, Math.min(node.path("summary").asText("").length(), 1000));
            List<String> keyInsights = parseStringList(node.get("keyInsights"));
            String sentiment = normalizeSentiment(node.path("sentiment").asText("NEUTRAL"));
            double sentimentScore = Math.max(-1.0, Math.min(1.0, node.path("sentimentScore").asDouble(0.0)));
            List<String> tags = parseStringList(node.get("tags"));

            return new AiAnalysisResult(summary, keyInsights, sentiment, sentimentScore, tags, provider, model);
        } catch (Exception e) {
            log.warn("Failed to parse AI JSON response: {}", e.getMessage());
            return AiAnalysisResult.empty(provider, model);
        }
    }

    private static List<String> parseStringList(JsonNode node) {
        if (node == null || !node.isArray()) return List.of();
        return MAPPER.convertValue(node, new TypeReference<List<String>>() {});
    }

    private static String normalizeSentiment(String raw) {
        return switch (raw.toUpperCase()) {
            case "POSITIVE" -> "POSITIVE";
            case "NEGATIVE" -> "NEGATIVE";
            default -> "NEUTRAL";
        };
    }
}
