package com.example.backend.service;

import com.example.backend.dto.StatisticsDto;
import com.example.backend.dto.StatusCountDto;
import com.example.backend.dto.TrendCountDto;
import com.example.backend.dto.TypeCountDto;
import com.example.backend.dto.LocationCountDto;
import com.example.backend.exception.BizException;
import com.example.backend.repository.ViolationEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StatisticsService {
    private final ViolationEventRepository violationEventRepository;

    public StatisticsService(ViolationEventRepository violationEventRepository) {
        this.violationEventRepository = violationEventRepository;
    }

    /**
     * 统计指定日期范围内的违法总量、今日数量、类型分布、状态分布、趋势和地点排行。
     * 未传日期时，分布和排行默认统计全量数据；趋势图始终展示结束日期前 7 天。
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
        LocalDateTime start = startDate == null ? LocalDateTime.of(1970, 1, 1, 0, 0) : startDate.atStartOfDay();
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
        LocalDate trendEndDate = endDate == null ? LocalDate.now() : endDate;
        LocalDate trendStartDate = trendEndDate.minusDays(6);
        LocalDateTime trendStart = trendStartDate.atStartOfDay();
        LocalDateTime trendEnd = trendEndDate.plusDays(1).atStartOfDay();
        List<TrendCountDto> trend = buildSevenDayTrend(trendStartDate, trendEndDate, trendStart, trendEnd);
        List<LocationCountDto> locationRanking = violationEventRepository.countByLocationBetween(start, end, 10).stream()
                .map(item -> new LocationCountDto(item.getName(), item.getCount()))
                .toList();
        return new StatisticsDto(total, today, typeDistribution, statusDistribution, trend, locationRanking);
    }

    private List<TrendCountDto> buildSevenDayTrend(
            LocalDate trendStartDate,
            LocalDate trendEndDate,
            LocalDateTime trendStart,
            LocalDateTime trendEnd
    ) {
        Map<String, Long> countByDay = violationEventRepository.countByDayBetween(trendStart, trendEnd).stream()
                .collect(Collectors.toMap(
                        ViolationEventRepository.TrendCountProjection::getDay,
                        ViolationEventRepository.TrendCountProjection::getCount
                ));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        long days = trendEndDate.toEpochDay() - trendStartDate.toEpochDay();
        return IntStream.rangeClosed(0, (int) days)
                .mapToObj(offset -> {
                    LocalDate day = trendStartDate.plusDays(offset);
                    return new TrendCountDto(formatter.format(day), countByDay.getOrDefault(day.toString(), 0L));
                })
                .toList();
    }
}
