package com.example.backend.service;

import com.example.backend.exception.BizException;
import com.example.backend.repository.ViolationEventRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class StatisticsServiceTests {

    @Test
    void getRejectsEndDateBeforeStartDate() {
        StatisticsService service = new StatisticsService(mock(ViolationEventRepository.class));

        BizException exception = assertThrows(BizException.class,
                () -> service.get(LocalDate.of(2026, 5, 21), LocalDate.of(2026, 5, 20)));

        assertEquals(400, exception.getCode());
        assertEquals("endDate must not be before startDate", exception.getMessage());
    }
}
