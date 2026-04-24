package com.pulse.ai.model;

import java.util.List;

/**
 * Internal model representing the parsed AI response.
 * Used by both OpenAI and Claude clients.
 */
public record AiAnalysisResult(
        String summary,
        List<String> keyInsights,
        String sentiment,
        double sentimentScore,
        List<String> tags,
        String provider,
        String modelUsed
) {
    public static AiAnalysisResult empty(String provider, String model) {
        return new AiAnalysisResult("", List.of(), "NEUTRAL", 0.0, List.of(), provider, model);
    }
}
