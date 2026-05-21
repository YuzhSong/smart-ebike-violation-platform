package com.example.backend.dto;

import java.time.LocalDateTime;

public record ViolationDetailDto(
        Long id,
        Long userId,
        Long deviceId,
        String violationType,
        LocalDateTime eventTime,
        String status,
        String imageUrl,
        String remark
) {
}
