package com.pulse.knowledge.dto;

/**
 * Request body for PATCH /api/v1/bookmarks/{id}
 * All fields optional — null means no change.
 */
public record BookmarkUpdateRequest(
        String userNote,    // null = no change
        String status       // READ | UNREAD | ARCHIVED  (null = no change)
) {}
