package com.pulse.article.dto;

import com.pulse.article.entity.Source;

public record SourceDto(
        Long id,
        String name,
        String url,
        String feedUrl,
        String iconUrl,
        String type,
        String domain,
        String language,
        String status,
        Integer fetchIntervalMinutes,
        Integer errorCount,
        String lastFetchedAt
) {
    public static SourceDto from(Source s) {
        return new SourceDto(
                s.id,
                s.name,
                s.url,
                s.feedUrl,
                s.iconUrl,
                s.type,
                s.domain,
                s.language,
                s.status,
                s.fetchIntervalMinutes,
                s.errorCount,
                s.lastFetchedAt != null ? s.lastFetchedAt.toString() : null
        );
    }
}
