package com.example.backend.service;

import com.example.backend.common.PageResult;
import com.example.backend.dto.ViolationDetailDto;
import com.example.backend.dto.ViolationSummaryDto;
import com.example.backend.entity.ViolationEvent;
import com.example.backend.exception.BizException;
import com.example.backend.repository.ViolationEventRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ViolationQueryService {
    private final ViolationEventRepository violationEventRepository;

    public ViolationQueryService(ViolationEventRepository violationEventRepository) {
        this.violationEventRepository = violationEventRepository;
    }

    public List<ViolationSummaryDto> listByUserId(Long userId) {
        return violationEventRepository.findByUser_IdOrderByEventTimeDesc(userId).stream()
                .map(this::toSummary)
                .toList();
    }

    public ViolationDetailDto getById(Long id) {
        ViolationEvent event = violationEventRepository.findById(id)
                .orElseThrow(() -> new BizException(404, "violation not found"));
        return toDetail(event);
    }

    public PageResult<ViolationSummaryDto> listForAdmin(Integer page, Integer size, String status, Long deviceId) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        PageRequest pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "eventTime"));
        Specification<ViolationEvent> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (deviceId != null) {
                predicates.add(cb.equal(root.get("device").get("id"), deviceId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<ViolationEvent> eventPage = violationEventRepository.findAll(spec, pageable);
        List<ViolationSummaryDto> records = eventPage.stream().map(this::toSummary).toList();
        return new PageResult<>(eventPage.getTotalElements(), records);
    }

    private ViolationSummaryDto toSummary(ViolationEvent event) {
        return new ViolationSummaryDto(event.getId(), event.getViolationType(), event.getEventTime(), event.getStatus());
    }

    private ViolationDetailDto toDetail(ViolationEvent event) {
        Long userId = event.getUser() == null ? null : event.getUser().getId();
        Long deviceId = event.getDevice() == null ? null : event.getDevice().getId();
        return new ViolationDetailDto(
                event.getId(),
                userId,
                deviceId,
                event.getViolationType(),
                event.getEventTime(),
                event.getStatus(),
                event.getImageUrl(),
                event.getRemark()
        );
    }
}
