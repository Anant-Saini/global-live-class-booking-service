package com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs;

import lombok.Builder;
import java.util.List;

@Builder
public record OfferingWithSessionsResponseDTO(
    Long id,
    String courseName,
    String offeringName,
    String teacherName,
    Integer maxSeats,
    Integer bookedSeats,
    List<SessionResponseDTO> sessions
) {}