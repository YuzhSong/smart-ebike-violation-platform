package com.example.backend.service;

import com.example.backend.dto.StatisticsDto;
import com.example.backend.dto.StatusCountDto;
import com.example.backend.dto.TypeCountDto;
import com.example.backend.repository.ViolationEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticsService {
    private final ViolationEventRepository violationEventRepository;

    public StatisticsService(ViolationEventRepository violationEventRepository) {
        this.violationEventRepository = violationEventRepository;
    }

    public StatisticsDto get(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate == null ? LocalDate.now().minusDays(30).atStartOfDay() : startDate.atStartOfDay();
        LocalDateTime end = endDate == null ? LocalDate.now().plusDays(1).atStartOfDay() : endDate.plusDays(1).atStartOfDay();
        long total = violationEventRepository.countByEventTimeBetween(start, end);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();
        long today = violationEventRepository.countByEventTimeBetween(todayStart, tomorrowStart);
        List<TypeCountDto> typeDistribution = violationEventRepository.countByTypeBetween(start, end).stream()
                .map(item -> new TypeCountDto(item.getType(), item.getCount()))
                .toList();
        List<StatusCountDto> statusDistribution = violationEventRepository.countByStatusBetween(start, end).stream()
                .map(item -> new StatusCountDto(item.getStatus(), item.getCount()))
                .toList();
        return new StatisticsDto(total, today, typeDistribution, statusDistribution);
    }
}
