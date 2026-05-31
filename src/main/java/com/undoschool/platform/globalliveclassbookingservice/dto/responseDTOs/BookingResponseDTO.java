package com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs;

import lombok.Builder;
import java.time.ZonedDateTime;

@Builder
public record BookingResponseDTO(
    Long bookingId,
    Long offeringId,
    String courseName,
    String offeringName,
    String teacherName,
    ZonedDateTime bookedAt
) {}