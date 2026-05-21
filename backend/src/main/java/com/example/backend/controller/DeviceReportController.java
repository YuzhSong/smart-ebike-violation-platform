package com.example.backend.controller;

import com.example.backend.common.ApiResponse;
import com.example.backend.dto.DeviceReportResultDto;
import com.example.backend.service.DeviceReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/device")
public class DeviceReportController {
    private final DeviceReportService deviceReportService;

    public DeviceReportController(DeviceReportService deviceReportService) {
        this.deviceReportService = deviceReportService;
    }

    @PostMapping("/report")
    public ApiResponse<DeviceReportResultDto> report(
            @RequestParam("deviceCode") String deviceCode,
            @RequestParam(value = "captureTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime captureTime,
            @RequestParam("image") MultipartFile image
    ) {
        DeviceReportResultDto data = deviceReportService.report(deviceCode, captureTime, image);
        return ApiResponse.success("report accepted", data);
    }
}
