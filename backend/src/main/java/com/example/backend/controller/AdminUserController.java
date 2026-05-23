package com.example.backend.controller;

import com.example.backend.common.ApiResponse;
import com.example.backend.dto.AdminUserDto;
import com.example.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<AdminUserDto>> list() {
        return ApiResponse.success(userService.listForAdmin());
    }
}
