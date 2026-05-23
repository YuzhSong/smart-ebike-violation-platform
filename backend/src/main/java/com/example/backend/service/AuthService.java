package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.UserInfo;
import com.example.backend.exception.BizException;
import com.example.backend.repository.UserInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {
    private final UserInfoRepository userInfoRepository;
    private final PasswordHashService passwordHashService;

    public AuthService(UserInfoRepository userInfoRepository, PasswordHashService passwordHashService) {
        this.userInfoRepository = userInfoRepository;
        this.passwordHashService = passwordHashService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String account = request.account() == null ? "" : request.account().trim();
        UserInfo user = userInfoRepository.findByAccount(account)
                .orElseThrow(() -> new BizException(401, "invalid account or password"));

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BizException(403, "user is disabled");
        }
        if (!passwordHashService.matches(request.password(), user.getPasswordHash())) {
            throw new BizException(401, "invalid account or password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userInfoRepository.save(user);

        return new LoginResponse(
                user.getId(),
                user.getAccount(),
                user.getUsername(),
                user.getPhone(),
                "Bearer",
                createDevelopmentToken(user)
        );
    }

    private String createDevelopmentToken(UserInfo user) {
        String value = user.getId() + ":" + user.getAccount() + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
