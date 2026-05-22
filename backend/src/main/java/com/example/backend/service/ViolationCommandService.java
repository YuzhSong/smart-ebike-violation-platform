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
    private static final int MAX_REMARK_LENGTH = 255;
    private final ViolationEventRepository violationEventRepository;

    public ViolationCommandService(ViolationEventRepository violationEventRepository) {
        this.violationEventRepository = violationEventRepository;
    }

    /**
     * 更新违法事件审核状态和备注，终态会记录审核时间，非终态会清空审核时间。
     *
     * @param id 违法事件 ID
     * @param status 新状态，必须属于文档约定枚举
     * @param remark 审核备注，可为空，最长 255 字符
     * @return 更新成功返回 true
     * @throws BizException 状态非法、备注过长或事件不存在时抛出
     */
    @Transactional
    public boolean updateStatus(Long id, String status, String remark) {
        String normalizedStatus = status == null ? "" : status.trim();
        if (!ALLOWED_STATUS.contains(normalizedStatus)) {
            throw new BizException(400, "invalid status");
        }
        if (remark != null && remark.length() > MAX_REMARK_LENGTH) {
            throw new BizException(400, "remark too long");
        }
        ViolationEvent event = violationEventRepository.findById(id)
                .orElseThrow(() -> new BizException(404, "violation not found"));
        event.setStatus(normalizedStatus);
        event.setRemark(remark);
        if ("CONFIRMED".equals(normalizedStatus) || "REJECTED".equals(normalizedStatus)) {
            event.setReviewTime(LocalDateTime.now());
        } else {
            event.setReviewTime(null);
        }
        violationEventRepository.save(event);
        return true;
    }
}
