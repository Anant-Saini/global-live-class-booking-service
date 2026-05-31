package com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs;

import java.math.BigDecimal;

public record OfferingResponseDTO(
        Long id,
        String courseTitle,
        String teacherName,
        String name,
        BigDecimal price,
        String currency,
        Integer totalSeats,
        Integer bookedSeats
) {}
