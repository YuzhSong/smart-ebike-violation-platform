package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateViolationStatusRequest(
        @NotBlank(message = "status is required")
        String status,
        String remark
) {
}
