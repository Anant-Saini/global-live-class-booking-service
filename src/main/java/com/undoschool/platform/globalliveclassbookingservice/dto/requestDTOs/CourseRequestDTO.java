package com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs;

import jakarta.validation.constraints.NotBlank;

public record CourseRequestDTO(
        @NotBlank String title,
        String description
) {}