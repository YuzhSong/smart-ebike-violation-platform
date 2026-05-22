USE traffic_platform;

INSERT INTO user_info (id, username, phone) VALUES
(1, 'test_user', '13800000001')
ON DUPLICATE KEY UPDATE
username = VALUES(username),
phone = VALUES(phone);

INSERT INTO admin_info (id, admin_name, account, password_hash) VALUES
(1, 'system_admin', 'admin', 'admin123_hash_placeholder')
ON DUPLICATE KEY UPDATE
admin_name = VALUES(admin_name),
account = VALUES(account),
password_hash = VALUES(password_hash);

INSERT INTO device_info (id, device_code, location_desc, status) VALUES
(1, 'DEV-001', '东城区路口A', 'ONLINE'),
(2, 'DEV-002', '西城区路口B', 'ONLINE')
ON DUPLICATE KEY UPDATE
device_code = VALUES(device_code),
location_desc = VALUES(location_desc),
status = VALUES(status);

INSERT INTO violation_event (
    id,
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
(1, 1, 1, '未佩戴头盔', NOW() - INTERVAL 1 DAY, 'PENDING', 0.9300, '[128,64,320,280]', '{"message":"mock detect success","result":[{"label":"未佩戴头盔","confidence":0.93,"bbox":[128,64,320,280]}]}', '/images/event_001.jpg'),
(2, 1, 2, '闯红灯', NOW() - INTERVAL 12 HOUR, 'REVIEWING', 0.8800, '[80,50,260,240]', '{"message":"mock detect success","result":[{"label":"闯红灯","confidence":0.88,"bbox":[80,50,260,240]}]}', '/images/event_002.jpg'),
(3, NULL, 1, '逆行', NOW() - INTERVAL 2 HOUR, 'CONFIRMED', 0.9100, '[100,70,300,260]', '{"message":"mock detect success","result":[{"label":"逆行","confidence":0.91,"bbox":[100,70,300,260]}]}', '/images/event_003.jpg')
ON DUPLICATE KEY UPDATE
user_id = VALUES(user_id),
device_id = VALUES(device_id),
violation_type = VALUES(violation_type),
event_time = VALUES(event_time),
status = VALUES(status),
confidence = VALUES(confidence),
bbox = VALUES(bbox),
model_result = VALUES(model_result),
image_url = VALUES(image_url);

UPDATE violation_event
SET remark = '审核通过', review_time = NOW() - INTERVAL 1 HOUR
WHERE id = 3;
