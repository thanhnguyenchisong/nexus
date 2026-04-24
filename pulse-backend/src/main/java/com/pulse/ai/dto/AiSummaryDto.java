package com.pulse.ai.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.ai.entity.AiSummary;

import java.util.List;

public record AiSummaryDto(
        Long id,
        Long articleId,
        String summary,
        List<String> keyInsights,
        String sentiment,
        Double sentimentScore,
        List<String> tags,
        String aiProvider,
        String modelUsed,
        String generatedAt
) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static AiSummaryDto from(AiSummary s) {
        return new AiSummaryDto(
                s.id,
                s.article != null ? s.article.id : null,
                s.summary,
                parseJsonList(s.keyInsights),
                s.sentiment,
                s.sentimentScore,
                parseJsonList(s.tags),
                s.aiProvider,
                s.modelUsed,
                s.generatedAt != null ? s.generatedAt.toString() : null
        );
    }

    private static List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
