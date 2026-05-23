package com.example.backend.dto;

public record LoginResponse(
        Long userId,
        String account,
        String username,
        String phone,
        String tokenType,
        String accessToken
) {
}
