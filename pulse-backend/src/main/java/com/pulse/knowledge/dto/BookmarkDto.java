package com.pulse.knowledge.dto;

import com.pulse.article.dto.ArticleDto;
import com.pulse.knowledge.entity.SavedArticle;

public record BookmarkDto(
        Long id,
        ArticleDto article,
        String userNote,
        String status,
        String savedAt,
        String updatedAt
) {
    public static BookmarkDto from(SavedArticle s) {
        return new BookmarkDto(
                s.id,
                ArticleDto.from(s.article),
                s.userNote,
                s.status,
                s.savedAt  != null ? s.savedAt.toString()  : null,
                s.updatedAt != null ? s.updatedAt.toString() : null
        );
    }
}
