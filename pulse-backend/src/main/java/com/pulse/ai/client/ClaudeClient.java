package com.pulse.ai.client;

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
public class ClaudeClient {

    private static final Logger log = LoggerFactory.getLogger(ClaudeClient.class);
    private static final String ENDPOINT = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-haiku-4-5";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @ConfigProperty(name = "claude.api.key", defaultValue = "")
    String apiKey;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public AiAnalysisResult analyze(String prompt) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("${CLAUDE_API_KEY}")) {
            throw new AiProviderException("Claude API key not configured");
        }

        try {
            String requestBody = MAPPER.writeValueAsString(Map.of(
                    "model", MODEL,
                    "max_tokens", 600,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("Claude returned HTTP {}: {}", response.statusCode(), response.body());
                throw new AiProviderException("Claude HTTP " + response.statusCode());
            }

            JsonNode root = MAPPER.readTree(response.body());
            // Claude response: content[0].text
            String content = root.path("content").get(0).path("text").asText();
            return OpenAiClient.parseAiJson(content, "CLAUDE", MODEL);

        } catch (AiProviderException e) {
            throw e;
        } catch (Exception e) {
            log.error("Claude request failed: {}", e.getMessage());
            throw new AiProviderException("Claude error: " + e.getMessage(), e);
        }
    }
}
