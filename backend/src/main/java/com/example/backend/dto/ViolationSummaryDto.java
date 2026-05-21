package com.example.backend.dto;

import java.time.LocalDateTime;

public record ViolationSummaryDto(Long id, String violationType, LocalDateTime eventTime, String status) {
}
