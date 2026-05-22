package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ViolationDetailDto(
        Long id,
        Long userId,
        Long deviceId,
        String violationType,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventTime,
        String status,
        String imageUrl,
        String remark
) {
}
