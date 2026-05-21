package com.example.backend.service;

import com.example.backend.entity.ViolationEvent;
import com.example.backend.exception.BizException;
import com.example.backend.repository.ViolationEventRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ViolationCommandServiceTests {

    @Test
    void updateStatusSetsReviewTimeForFinalStatus() {
        ViolationEventRepository repository = mock(ViolationEventRepository.class);
        ViolationEvent event = new ViolationEvent();
        when(repository.findById(1L)).thenReturn(Optional.of(event));
        when(repository.save(any(ViolationEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ViolationCommandService service = new ViolationCommandService(repository);

        service.updateStatus(1L, " CONFIRMED ", "审核通过");

        assertEquals("CONFIRMED", event.getStatus());
        assertEquals("审核通过", event.getRemark());
        assertNotNull(event.getReviewTime());
        verify(repository).save(event);
    }

    @Test
    void updateStatusRejectsInvalidStatus() {
        ViolationEventRepository repository = mock(ViolationEventRepository.class);
        ViolationCommandService service = new ViolationCommandService(repository);

        BizException exception = assertThrows(BizException.class,
                () -> service.updateStatus(1L, "DONE", null));

        assertEquals(400, exception.getCode());
        assertEquals("invalid status", exception.getMessage());
    }
}
