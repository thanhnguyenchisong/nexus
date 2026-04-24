package com.pulse.article.dto;

import com.pulse.article.entity.Article;

public record ArticleDto(
        Long id,
        String title,
        String description,
        String originalUrl,
        String author,
        String thumbnailUrl,
        String domain,
        String category,
        String language,
        String publishedAt,
        String createdAt,
        SourceSummary source
) {
    public record SourceSummary(Long id, String name, String iconUrl, String domain) {}

    public static ArticleDto from(Article a) {
        SourceSummary src = null;
        if (a.source != null) {
            try {
                src = new SourceSummary(a.source.id, a.source.name, a.source.iconUrl, a.source.domain);
            } catch (Exception ignored) { }
        }
        return new ArticleDto(
                a.id,
                a.title,
                a.description,
                a.originalUrl,
                a.author,
                a.thumbnailUrl,
                a.domain,
                a.category,
                a.language,
                a.publishedAt != null ? a.publishedAt.toString() : null,
                a.createdAt != null ? a.createdAt.toString() : null,
                src
        );
    }
}
