package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ViolationSummaryDto(
        Long id,
        Long userId,
        String userAccount,
        String username,
        Long deviceId,
        String deviceCode,
        String locationDesc,
        String violationType,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventTime,
        String status,
        String remark,
        BigDecimal confidence
) {
}
