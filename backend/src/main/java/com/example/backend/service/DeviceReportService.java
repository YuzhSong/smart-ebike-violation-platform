package com.example.backend.service;

import com.example.backend.dto.DeviceReportResultDto;
import com.example.backend.dto.ModelDetectResultDto;
import com.example.backend.entity.DeviceInfo;
import com.example.backend.entity.ViolationEvent;
import com.example.backend.exception.BizException;
import com.example.backend.repository.DeviceInfoRepository;
import com.example.backend.repository.ViolationEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class DeviceReportService {
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");
    private final DeviceInfoRepository deviceInfoRepository;
    private final ViolationEventRepository violationEventRepository;
    private final ModelDetectService modelDetectService;
    private final ObjectMapper objectMapper;
    private final Path uploadDir;

    public DeviceReportService(
            DeviceInfoRepository deviceInfoRepository,
            ViolationEventRepository violationEventRepository,
            ModelDetectService modelDetectService,
            ObjectMapper objectMapper,
            @Value("${app.upload-dir:uploads}") String uploadDir
    ) {
        this.deviceInfoRepository = deviceInfoRepository;
        this.violationEventRepository = violationEventRepository;
        this.modelDetectService = modelDetectService;
        this.objectMapper = objectMapper;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * 处理设备上报图片：校验设备和抓拍参数，调用模型服务识别，保存图片并生成待处理违法事件。
     *
     * @param deviceCode 设备编号，必须能在 device_info 中查到
     * @param captureTime 抓拍时间
     * @param image 抓拍图片文件
     * @return 新增违法事件 ID
     * @throws BizException 参数缺失、设备不存在、模型服务异常或图片保存失败时抛出
     */
    @Transactional
    public DeviceReportResultDto report(String deviceCode, LocalDateTime captureTime, MultipartFile image) {
        if (deviceCode == null || deviceCode.isBlank()) {
            throw new BizException(400, "deviceCode is required");
        }
        if (captureTime == null) {
            throw new BizException(400, "captureTime is required");
        }
        if (image == null || image.isEmpty()) {
            throw new BizException(400, "image is required");
        }
        DeviceInfo device = deviceInfoRepository.findByDeviceCode(deviceCode.trim())
                .orElseThrow(() -> new BizException(404, "device not found"));
        ModelDetectService.DetectResult detectResult = modelDetectService.detect(image);
        ModelDetectResultDto modelResult = detectResult.firstResult();
        String storedName = storeImage(image);
        ViolationEvent event = new ViolationEvent();
        event.setDevice(device);
        event.setEventTime(captureTime);
        event.setStatus("PENDING");
        event.setViolationType(modelResult.label());
        event.setConfidence(normalizeConfidence(modelResult.confidence()));
        event.setBbox(toJson(modelResult.bbox()));
        event.setModelResult(detectResult.rawResponse());
        event.setImageUrl("/uploads/" + storedName);
        ViolationEvent saved = violationEventRepository.save(event);
        return new DeviceReportResultDto(saved.getId());
    }

    private BigDecimal normalizeConfidence(BigDecimal confidence) {
        return confidence == null ? null : confidence.setScale(4, RoundingMode.HALF_UP);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BizException(500, "failed to serialize model result");
        }
    }

    private String storeImage(MultipartFile image) {
        try {
            Files.createDirectories(uploadDir);
            String ext = resolveSafeExtension(image.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(filename);
            Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new BizException(500, "failed to store image");
        }
    }

    // 中文注释：只保留常见图片后缀，避免上传文件名中的异常后缀直接成为静态资源路径。
    private String resolveSafeExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return ".jpg";
        }
        String ext = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
        return ALLOWED_IMAGE_EXTENSIONS.contains(ext) ? ext : ".jpg";
    }
}
