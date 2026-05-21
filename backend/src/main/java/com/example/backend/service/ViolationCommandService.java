package com.example.backend.service;

import com.example.backend.entity.ViolationEvent;
import com.example.backend.exception.BizException;
import com.example.backend.repository.ViolationEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class ViolationCommandService {
    private static final Set<String> ALLOWED_STATUS = Set.of("PENDING", "REVIEWING", "CONFIRMED", "REJECTED");
    private final ViolationEventRepository violationEventRepository;

    public ViolationCommandService(ViolationEventRepository violationEventRepository) {
        this.violationEventRepository = violationEventRepository;
    }

    @Transactional
    public boolean updateStatus(Long id, String status, String remark) {
        if (!ALLOWED_STATUS.contains(status)) {
            throw new BizException(400, "invalid status");
        }
        ViolationEvent event = violationEventRepository.findById(id)
                .orElseThrow(() -> new BizException(404, "violation not found"));
        event.setStatus(status);
        event.setRemark(remark);
        if ("CONFIRMED".equals(status) || "REJECTED".equals(status)) {
            event.setReviewTime(LocalDateTime.now());
        }
        violationEventRepository.save(event);
        return true;
    }
}
