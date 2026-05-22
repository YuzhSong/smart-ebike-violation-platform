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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ViolationQueryService {
    private static final Set<String> ALLOWED_STATUS = Set.of("PENDING", "REVIEWING", "CONFIRMED", "REJECTED");
    private final ViolationEventRepository violationEventRepository;

    public ViolationQueryService(ViolationEventRepository violationEventRepository) {
        this.violationEventRepository = violationEventRepository;
    }

    /**
     * 查询指定用户的违法事件摘要列表，按事件时间倒序返回。
     *
     * @param userId 普通用户 ID，必须为正数
     * @return 违法事件摘要列表
     * @throws BizException userId 非法时抛出
     */
    public List<ViolationSummaryDto> listByUserId(Long userId) {
        if (userId == null || userId < 1) {
            throw new BizException(400, "invalid userId");
        }
        return violationEventRepository.findByUser_IdOrderByEventTimeDesc(userId).stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * 查询单条违法事件详情，包含用户 ID、设备 ID、状态、图片路径和审核备注。
     *
     * @param id 违法事件 ID
     * @return 违法事件详情
     * @throws BizException 事件不存在时抛出
     */
    public ViolationDetailDto getById(Long id) {
        ViolationEvent event = violationEventRepository.findById(id)
                .orElseThrow(() -> new BizException(404, "violation not found"));
        return toDetail(event);
    }

    /**
     * 管理员分页查询违法事件，可按状态和设备筛选。
     *
     * @param page 页码，从 1 开始，非法值会回退到默认值
     * @param size 每页数量，最大限制为 100
     * @param status 事件状态，可为空，必须属于文档约定枚举
     * @param deviceId 设备 ID，可为空，必须为正数
     * @return 分页违法事件摘要
     * @throws BizException status 或 deviceId 非法时抛出
     */
    public PageResult<ViolationSummaryDto> listForAdmin(Integer page, Integer size, String status, Long deviceId) {
        String normalizedStatus = normalizeStatus(status);
        if (deviceId != null && deviceId < 1) {
            throw new BizException(400, "invalid deviceId");
        }
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        PageRequest pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "eventTime"));
        Specification<ViolationEvent> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (normalizedStatus != null) {
                predicates.add(cb.equal(root.get("status"), normalizedStatus));
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

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String normalizedStatus = status.trim();
        if (!ALLOWED_STATUS.contains(normalizedStatus)) {
            throw new BizException(400, "invalid status");
        }
        return normalizedStatus;
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
