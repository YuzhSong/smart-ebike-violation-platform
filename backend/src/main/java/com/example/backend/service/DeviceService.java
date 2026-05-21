package com.example.backend.service;

import com.example.backend.dto.DeviceDto;
import com.example.backend.entity.DeviceInfo;
import com.example.backend.repository.DeviceInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {
    private final DeviceInfoRepository deviceInfoRepository;

    public DeviceService(DeviceInfoRepository deviceInfoRepository) {
        this.deviceInfoRepository = deviceInfoRepository;
    }

    /**
     * 查询设备列表，可按设备状态和设备编号/点位关键字过滤。
     *
     * @param status 设备状态，可为空
     * @param keyword 设备编号或安装点位关键字，可为空
     * @return 设备列表
     */
    public List<DeviceDto> list(String status, String keyword) {
        return deviceInfoRepository.findAll().stream()
                .filter(device -> status == null || status.isBlank() || status.equals(device.getStatus()))
                .filter(device -> keyword == null || keyword.isBlank() || containsKeyword(device, keyword))
                .map(this::toDto)
                .toList();
    }

    private boolean containsKeyword(DeviceInfo device, String keyword) {
        return device.getDeviceCode().contains(keyword) || device.getLocationDesc().contains(keyword);
    }

    private DeviceDto toDto(DeviceInfo device) {
        return new DeviceDto(device.getId(), device.getDeviceCode(), device.getStatus(), device.getLocationDesc());
    }
}
