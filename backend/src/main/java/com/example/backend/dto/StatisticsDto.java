package com.example.backend.dto;

import java.util.List;

public record StatisticsDto(long totalViolations, long todayViolations, List<TypeCountDto> typeDistribution) {
}
