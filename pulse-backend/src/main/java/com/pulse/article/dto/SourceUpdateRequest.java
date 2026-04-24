package com.pulse.article.dto;

/**
 * Request body for PATCH /api/v1/sources/{id}
 * All fields are optional — null means "no change".
 */
public record SourceUpdateRequest(
        String status,            // ACTIVE | INACTIVE | ERROR
        Integer fetchIntervalMinutes
) {}
