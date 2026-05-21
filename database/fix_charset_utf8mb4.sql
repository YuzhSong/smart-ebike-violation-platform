ALTER DATABASE traffic_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE traffic_platform;

ALTER TABLE user_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE admin_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE device_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE violation_event CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
