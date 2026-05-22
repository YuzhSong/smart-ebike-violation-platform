package com.example.backend.service;

import com.example.backend.exception.BizException;
import com.example.backend.repository.ViolationEventRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class ViolationQueryServiceTests {

    @Test
    void listByUserIdRejectsInvalidUserId() {
        ViolationEventRepository repository = mock(ViolationEventRepository.class);
        ViolationQueryService service = new ViolationQueryService(repository);

        BizException exception = assertThrows(BizException.class, () -> service.listByUserId(0L));

        assertEquals(400, exception.getCode());
        assertEquals("invalid userId", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void listForAdminRejectsInvalidStatus() {
        ViolationEventRepository repository = mock(ViolationEventRepository.class);
        ViolationQueryService service = new ViolationQueryService(repository);

        BizException exception = assertThrows(BizException.class,
                () -> service.listForAdmin(1, 10, "DONE", null));

        assertEquals(400, exception.getCode());
        assertEquals("invalid status", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void listForAdminRejectsInvalidDeviceId() {
        ViolationEventRepository repository = mock(ViolationEventRepository.class);
        ViolationQueryService service = new ViolationQueryService(repository);

        BizException exception = assertThrows(BizException.class,
                () -> service.listForAdmin(1, 10, null, 0L));

        assertEquals(400, exception.getCode());
        assertEquals("invalid deviceId", exception.getMessage());
        verifyNoInteractions(repository);
    }
}
