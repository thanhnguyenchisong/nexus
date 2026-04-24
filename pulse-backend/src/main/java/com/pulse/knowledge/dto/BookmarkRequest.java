package com.pulse.knowledge.dto;

/**
 * Request body for POST /api/v1/bookmarks (create bookmark)
 */
public record BookmarkRequest(
        Long articleId,
        String userNote   // optional
) {}
