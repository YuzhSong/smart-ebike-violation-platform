package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ModelDetectResultDto(String label, BigDecimal confidence, List<Integer> bbox) {
}
