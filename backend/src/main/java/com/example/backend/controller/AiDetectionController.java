package com.example.backend.controller;

import com.example.backend.common.ApiResponse;
import com.example.backend.dto.ModelDetectResultDto;
import com.example.backend.service.ModelDetectService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
public class AiDetectionController {
    private final ModelDetectService modelDetectService;

    public AiDetectionController(ModelDetectService modelDetectService) {
        this.modelDetectService = modelDetectService;
    }

    @PostMapping("/detect/image")
    public ApiResponse<ModelDetectResultDto> detectImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(modelDetectService.detect(file).firstResult());
    }

    @PostMapping("/detect/video")
    public ApiResponse<JsonNode> detectVideo(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success("video accepted", modelDetectService.detectVideo(file));
    }
}
