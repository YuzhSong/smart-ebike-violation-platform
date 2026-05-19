USE traffic_platform;

INSERT INTO user_info (username, phone) VALUES
('test_user', '13800000001');

INSERT INTO admin_info (admin_name, account, password_hash) VALUES
('system_admin', 'admin', 'admin123_hash_placeholder');

INSERT INTO device_info (device_code, location_desc, status) VALUES
('DEV-001', '东城区路口A', 'ONLINE'),
('DEV-002', '西城区路口B', 'ONLINE');

INSERT INTO violation_event (user_id, device_id, violation_type, event_time, status, image_url) VALUES
(1, 1, '未佩戴头盔', NOW() - INTERVAL 1 DAY, 'PENDING', '/images/event_001.jpg'),
(1, 2, '闯红灯', NOW() - INTERVAL 12 HOUR, 'REVIEWING', '/images/event_002.jpg'),
(NULL, 1, '逆行', NOW() - INTERVAL 2 HOUR, 'CONFIRMED', '/images/event_003.jpg');
