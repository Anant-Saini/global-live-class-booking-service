package com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank String name,
        @NotBlank String role, // TEACHER or PARENT
        @NotBlank String timezoneId // e.g., "Asia/Kolkata"
) {}