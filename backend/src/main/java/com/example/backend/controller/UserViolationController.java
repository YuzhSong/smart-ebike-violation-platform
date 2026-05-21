package com.example.backend.controller;

import com.example.backend.common.ApiResponse;
import com.example.backend.dto.ViolationDetailDto;
import com.example.backend.dto.ViolationSummaryDto;
import com.example.backend.service.ViolationQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/violations")
public class UserViolationController {
    private final ViolationQueryService violationQueryService;

    public UserViolationController(ViolationQueryService violationQueryService) {
        this.violationQueryService = violationQueryService;
    }

    @GetMapping
    public ApiResponse<List<ViolationSummaryDto>> listByUser(@RequestParam("userId") Long userId) {
        return ApiResponse.success(violationQueryService.listByUserId(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ViolationDetailDto> getDetail(@PathVariable("id") Long id) {
        return ApiResponse.success(violationQueryService.getById(id));
    }
}
