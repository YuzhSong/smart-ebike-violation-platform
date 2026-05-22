package com.example.backend.controller;

import com.example.backend.common.ApiResponse;
import com.example.backend.common.PageResult;
import com.example.backend.dto.UpdateViolationStatusRequest;
import com.example.backend.dto.ViolationDetailDto;
import com.example.backend.dto.ViolationSummaryDto;
import com.example.backend.service.ViolationCommandService;
import com.example.backend.service.ViolationQueryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/violations")
public class AdminViolationController {
    private final ViolationQueryService violationQueryService;
    private final ViolationCommandService violationCommandService;

    public AdminViolationController(
            ViolationQueryService violationQueryService,
            ViolationCommandService violationCommandService
    ) {
        this.violationQueryService = violationQueryService;
        this.violationCommandService = violationCommandService;
    }

    @GetMapping
    public ApiResponse<PageResult<ViolationSummaryDto>> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "deviceId", required = false) Long deviceId
    ) {
        return ApiResponse.success(violationQueryService.listForAdmin(page, size, status, deviceId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ViolationDetailDto> detail(@PathVariable("id") Long id) {
        return ApiResponse.success(violationQueryService.getById(id));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateViolationStatusRequest request
    ) {
        boolean result = violationCommandService.updateStatus(id, request.status(), request.remark());
        return ApiResponse.success("status updated", result);
    }
}
