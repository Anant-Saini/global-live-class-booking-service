package com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs;

import java.time.Instant;

public record ErrorResponse(
        // Represents a precise moment in UTC
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
    // Ensure the timestamp is always set on creation
    public ErrorResponse {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}