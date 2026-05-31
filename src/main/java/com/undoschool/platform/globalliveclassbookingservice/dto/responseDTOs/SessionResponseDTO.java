package com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs;

import lombok.Builder;
import java.time.ZonedDateTime;

@Builder
public record SessionResponseDTO(
    Long id,
    Long offeringId,
    Long teacherId,
    String teacherName,
    ZonedDateTime startTime,
    ZonedDateTime endTime
) {}