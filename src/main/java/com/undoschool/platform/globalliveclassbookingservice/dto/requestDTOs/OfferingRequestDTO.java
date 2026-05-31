package com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OfferingRequestDTO(
        @NotNull Long courseId,
        @NotBlank String name,
        @NotNull BigDecimal price,
        @NotBlank String currency,
        @NotNull @Min(1) Integer totalSeats
) {}
