package com.example.backend.service;

import com.example.backend.dto.StatisticsDto;
import com.example.backend.dto.StatusCountDto;
import com.example.backend.dto.TypeCountDto;
import com.example.backend.exception.BizException;
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

    /**
     * 统计指定日期范围内的违法总量、今日数量、类型分布和状态分布。
     *
     * @param startDate 开始日期，可为空，默认最近 30 天
     * @param endDate 结束日期，可为空，默认今天
     * @return 统计结果
     * @throws BizException endDate 早于 startDate 时抛出
     */
    public StatisticsDto get(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BizException(400, "endDate must not be before startDate");
        }
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
