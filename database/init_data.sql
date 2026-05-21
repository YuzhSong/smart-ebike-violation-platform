USE traffic_platform;

INSERT INTO user_info (username, phone) VALUES
('test_user', '13800000001');

INSERT INTO admin_info (admin_name, account, password_hash) VALUES
('system_admin', 'admin', 'admin123_hash_placeholder');

INSERT INTO device_info (device_code, location_desc, status) VALUES
('DEV-001', '东城区路口A', 'ONLINE'),
('DEV-002', '西城区路口B', 'ONLINE');

INSERT INTO violation_event (
    user_id,
    device_id,
    violation_type,
    event_time,
    status,
    confidence,
    bbox,
    model_result,
    image_url
) VALUES
(1, 1, '未佩戴头盔', NOW() - INTERVAL 1 DAY, 'PENDING', 0.9300, '[128,64,320,280]', '{"message":"mock detect success","result":[{"label":"未佩戴头盔","confidence":0.93,"bbox":[128,64,320,280]}]}', '/images/event_001.jpg'),
(1, 2, '闯红灯', NOW() - INTERVAL 12 HOUR, 'REVIEWING', 0.8800, '[80,50,260,240]', '{"message":"mock detect success","result":[{"label":"闯红灯","confidence":0.88,"bbox":[80,50,260,240]}]}', '/images/event_002.jpg'),
(NULL, 1, '逆行', NOW() - INTERVAL 2 HOUR, 'CONFIRMED', 0.9100, '[100,70,300,260]', '{"message":"mock detect success","result":[{"label":"逆行","confidence":0.91,"bbox":[100,70,300,260]}]}', '/images/event_003.jpg');

UPDATE violation_event
SET remark = '审核通过', review_time = NOW() - INTERVAL 1 HOUR
WHERE status = 'CONFIRMED';
