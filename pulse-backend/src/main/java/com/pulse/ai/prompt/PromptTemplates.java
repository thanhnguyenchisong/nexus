package com.pulse.ai.prompt;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PromptTemplates {

    private static final String SUMMARIZE_EN = """
            You are a news analyst. Analyze the following article and respond with a JSON object only.
            No markdown, no extra text — just valid JSON.

            Article title: %s
            Article description: %s

            Respond with exactly this JSON structure:
            {
              "summary": "<2-3 sentence summary, max 300 chars>",
              "keyInsights": ["<insight 1>", "<insight 2>", "<insight 3>"],
              "sentiment": "<POSITIVE|NEGATIVE|NEUTRAL>",
              "sentimentScore": <float from -1.0 to 1.0>,
              "tags": ["<tag1>", "<tag2>", "<tag3>", "<tag4>", "<tag5>"]
            }

            Rules:
            - summary: factual, concise, max 300 characters
            - keyInsights: exactly 3 bullet points, max 80 chars each
            - sentiment: exactly one of POSITIVE, NEGATIVE, NEUTRAL
            - sentimentScore: float, -1.0 (very negative) to 1.0 (very positive)
            - tags: 3-5 lowercase hyphenated keywords (e.g., "machine-learning", "interest-rates")
            """;

    private static final String SUMMARIZE_VI = """
            Bạn là chuyên gia phân tích tin tức. Hãy phân tích bài báo dưới đây và trả lời bằng JSON.
            Không dùng markdown, chỉ JSON hợp lệ.

            Tiêu đề: %s
            Mô tả: %s

            Trả lời đúng cấu trúc JSON này:
            {
              "summary": "<tóm tắt 2-3 câu, tối đa 300 ký tự>",
              "keyInsights": ["<điểm chính 1>", "<điểm chính 2>", "<điểm chính 3>"],
              "sentiment": "<POSITIVE|NEGATIVE|NEUTRAL>",
              "sentimentScore": <số thực từ -1.0 đến 1.0>,
              "tags": ["<tag1>", "<tag2>", "<tag3>", "<tag4>", "<tag5>"]
            }
            """;

    public String buildPrompt(String title, String description, String language) {
        String template = "vi".equals(language) ? SUMMARIZE_VI : SUMMARIZE_EN;
        String safeTitle = title != null ? title : "";
        String safeDesc = description != null ? description : "";
        // Truncate to keep within token budget
        if (safeTitle.length() > 300) safeTitle = safeTitle.substring(0, 300);
        if (safeDesc.length() > 1500) safeDesc = safeDesc.substring(0, 1500);
        return String.format(template, safeTitle, safeDesc);
    }
}
