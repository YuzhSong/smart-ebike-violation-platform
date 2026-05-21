package com.example.backend.controller;

import com.example.backend.common.ApiResponse;
import com.example.backend.dto.DeviceDto;
import com.example.backend.service.DeviceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/devices")
public class AdminDeviceController {
    private final DeviceService deviceService;

    public AdminDeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ApiResponse<List<DeviceDto>> list(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return ApiResponse.success(deviceService.list(status, keyword));
    }
}
