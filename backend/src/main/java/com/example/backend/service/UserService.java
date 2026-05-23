package com.example.backend.service;

import com.example.backend.dto.AdminUserDto;
import com.example.backend.entity.UserInfo;
import com.example.backend.entity.ViolationEvent;
import com.example.backend.repository.UserInfoRepository;
import com.example.backend.repository.ViolationEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserInfoRepository userInfoRepository;
    private final ViolationEventRepository violationEventRepository;

    public UserService(UserInfoRepository userInfoRepository, ViolationEventRepository violationEventRepository) {
        this.userInfoRepository = userInfoRepository;
        this.violationEventRepository = violationEventRepository;
    }

    public List<AdminUserDto> listForAdmin() {
        return userInfoRepository.findAll().stream()
                .map(this::toAdminDto)
                .toList();
    }

    private AdminUserDto toAdminDto(UserInfo user) {
        long violationCount = violationEventRepository.countByUser_Id(user.getId());
        ViolationEvent latest = violationEventRepository.findTopByUser_IdOrderByEventTimeDesc(user.getId());
        return new AdminUserDto(
                user.getId(),
                user.getAccount(),
                user.getUsername(),
                user.getPhone(),
                user.getStatus(),
                violationCount,
                latest == null ? null : latest.getEventTime(),
                user.getLastLoginAt()
        );
    }
}
