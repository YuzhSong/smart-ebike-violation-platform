CREATE DATABASE IF NOT EXISTS traffic_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE traffic_platform;

CREATE TABLE IF NOT EXISTS user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admin_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_name VARCHAR(64) NOT NULL,
    account VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS device_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_code VARCHAR(64) NOT NULL UNIQUE,
    location_desc VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ONLINE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS violation_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    device_id BIGINT NOT NULL,
    violation_type VARCHAR(128) NOT NULL,
    event_time DATETIME NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    remark VARCHAR(255) NULL,
    confidence DECIMAL(5,4) NULL,
    bbox VARCHAR(255) NULL,
    image_url VARCHAR(255) NULL,
    reviewed_at DATETIME NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_violation_user FOREIGN KEY (user_id) REFERENCES user_info(id),
    CONSTRAINT fk_violation_device FOREIGN KEY (device_id) REFERENCES device_info(id)
);

CREATE INDEX idx_violation_user_id ON violation_event(user_id);
CREATE INDEX idx_violation_device_id ON violation_event(device_id);
CREATE INDEX idx_violation_status ON violation_event(status);
CREATE INDEX idx_violation_event_time ON violation_event(event_time);
