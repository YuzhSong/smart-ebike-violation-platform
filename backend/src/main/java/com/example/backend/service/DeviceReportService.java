package com.example.backend.service;

import com.example.backend.dto.DeviceReportResultDto;
import com.example.backend.entity.DeviceInfo;
import com.example.backend.entity.ViolationEvent;
import com.example.backend.exception.BizException;
import com.example.backend.repository.DeviceInfoRepository;
import com.example.backend.repository.ViolationEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeviceReportService {
    private final DeviceInfoRepository deviceInfoRepository;
    private final ViolationEventRepository violationEventRepository;
    private final Path uploadDir;

    public DeviceReportService(
            DeviceInfoRepository deviceInfoRepository,
            ViolationEventRepository violationEventRepository,
            @Value("${app.upload-dir:uploads}") String uploadDir
    ) {
        this.deviceInfoRepository = deviceInfoRepository;
        this.violationEventRepository = violationEventRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public DeviceReportResultDto report(String deviceCode, LocalDateTime captureTime, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BizException(400, "image is required");
        }
        DeviceInfo device = deviceInfoRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new BizException(404, "device not found"));
        String storedName = storeImage(image);
        ViolationEvent event = new ViolationEvent();
        event.setDevice(device);
        event.setEventTime(captureTime == null ? LocalDateTime.now() : captureTime);
        event.setStatus("PENDING");
        event.setViolationType("未佩戴头盔");
        event.setConfidence(new BigDecimal("0.9000"));
        event.setBbox("[0,0,100,100]");
        event.setImageUrl("/uploads/" + storedName);
        ViolationEvent saved = violationEventRepository.save(event);
        return new DeviceReportResultDto(saved.getId());
    }

    private String storeImage(MultipartFile image) {
        try {
            Files.createDirectories(uploadDir);
            String originalName = image.getOriginalFilename();
            String ext = ".jpg";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(filename);
            Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new BizException(500, "failed to store image");
        }
    }
}
