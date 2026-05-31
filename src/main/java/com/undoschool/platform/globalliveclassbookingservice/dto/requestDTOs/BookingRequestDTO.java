package com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for booking an offering. 
 */
public record BookingRequestDTO(
    @NotNull(message = "Offering ID is required for booking")
    Long offeringId
) {}