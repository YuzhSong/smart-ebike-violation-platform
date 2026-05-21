package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_info")
public class DeviceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_code", nullable = false, length = 64)
    private String deviceCode;

    @Column(name = "location_desc", nullable = false, length = 255)
    private String locationDesc;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public String getStatus() {
        return status;
    }
}
