package com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Request DTO for creating a session.
 * Industry standard: Expects ISO-8601 string (e.g., "2023-12-01T18:00:00+05:30").
 */
public record SessionRequestDTO(
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    ZonedDateTime startTime,
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    ZonedDateTime endTime
) {
    // Compact Constructor for cross-field validation
    public SessionRequestDTO {
        if (startTime != null && endTime != null) {
            if (!startTime.isBefore(endTime)) {
                throw new IllegalArgumentException("Session start time must be strictly before end time.");
            }
        }
    }
}