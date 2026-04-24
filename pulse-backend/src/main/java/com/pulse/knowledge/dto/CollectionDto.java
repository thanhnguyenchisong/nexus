package com.pulse.knowledge.dto;

import com.pulse.knowledge.entity.Collection;

import java.util.List;

public record CollectionDto(
        Long id,
        String name,
        String description,
        int articleCount,
        String createdAt
) {
    public static CollectionDto from(Collection c) {
        return new CollectionDto(
                c.id,
                c.name,
                c.description,
                c.savedArticles != null ? c.savedArticles.size() : 0,
                c.createdAt != null ? c.createdAt.toString() : null
        );
    }
}
