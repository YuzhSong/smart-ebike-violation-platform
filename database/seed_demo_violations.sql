USE traffic_platform;

-- Demo data for local development only.
-- Reserved ranges:
--   user_info.id:        9001-9005
--   device_info.id:      9001-9008
--   violation_event.id:  100001-100100
-- These records are marked with [DEMO_TEST_DATA] so production data can be kept separate.

INSERT INTO user_info (id, username, phone) VALUES
(9001, 'demo_student_01', '13900009001'),
(9002, 'demo_student_02', '13900009002'),
(9003, 'demo_student_03', '13900009003'),
(9004, 'demo_student_04', '13900009004'),
(9005, 'demo_student_05', '13900009005')
ON DUPLICATE KEY UPDATE
username = VALUES(username),
phone = VALUES(phone);

INSERT INTO device_info (id, device_code, location_desc, status) VALUES
(9001, 'DEMO-SCH-EAST-GATE', '学校东门非机动车道', 'ONLINE'),
(9002, 'DEMO-SCH-SOUTH-GATE', '学校南门公交站旁', 'ONLINE'),
(9003, 'DEMO-SCH-WEST-GATE', '学校西门斑马线', 'ONLINE'),
(9004, 'DEMO-SCH-NORTH-GATE', '学校北门共享电动车停放点', 'ONLINE'),
(9005, 'DEMO-SCH-LIBRARY', '图书馆南侧慢行道', 'ONLINE'),
(9006, 'DEMO-SCH-DORM', '学生宿舍区主路口', 'ONLINE'),
(9007, 'DEMO-SCH-CANTEEN', '食堂北侧路口', 'ONLINE'),
(9008, 'DEMO-SCH-TEACHING', '教学楼群东侧路口', 'OFFLINE')
ON DUPLICATE KEY UPDATE
device_code = VALUES(device_code),
location_desc = VALUES(location_desc),
status = VALUES(status);

DELETE FROM violation_event
WHERE id BETWEEN 100001 AND 100100
  AND remark LIKE '[DEMO_TEST_DATA]%';

INSERT INTO violation_event (
    id,
    user_id,
    device_id,
    violation_type,
    event_time,
    status,
    remark,
    confidence,
    bbox,
    model_result,
    image_url,
    review_time
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100
),
demo_rows AS (
    SELECT
        n,
        100000 + n AS event_id,
        CASE WHEN n % 6 = 0 THEN NULL ELSE 9001 + (n % 5) END AS demo_user_id,
        9001 + ((n - 1) % 8) AS demo_device_id,
        CASE n % 5
            WHEN 0 THEN '未佩戴头盔'
            WHEN 1 THEN '闯红灯'
            WHEN 2 THEN '逆行'
            WHEN 3 THEN '违规载人'
            ELSE '占用机动车道'
        END AS demo_type,
        TIMESTAMP(
            DATE_ADD('2026-02-01', INTERVAL FLOOR((n - 1) * 111 / 99) DAY),
            MAKETIME(7 + (n % 12), (n * 7) % 60, 0)
        ) AS demo_event_time,
        CASE n % 4
            WHEN 0 THEN 'PENDING'
            WHEN 1 THEN 'REVIEWING'
            WHEN 2 THEN 'CONFIRMED'
            ELSE 'REJECTED'
        END AS demo_status,
        ROUND(0.7600 + ((n * 37) % 2100) / 10000, 4) AS demo_confidence,
        CONCAT('[', 60 + (n * 11) % 180, ',', 40 + (n * 7) % 120, ',', 260 + (n * 13) % 220, ',', 210 + (n * 17) % 180, ']') AS demo_bbox
    FROM seq
)
SELECT
    event_id,
    demo_user_id,
    demo_device_id,
    demo_type,
    demo_event_time,
    demo_status,
    CONCAT('[DEMO_TEST_DATA] 学校周边电动车违章测试数据，批次 local-20260523，序号 ', LPAD(n, 3, '0')),
    demo_confidence,
    demo_bbox,
    JSON_OBJECT(
        'message', 'demo detect success',
        'source', 'local_demo_seed',
        'result', JSON_ARRAY(JSON_OBJECT(
            'label', demo_type,
            'confidence', demo_confidence,
            'bbox', JSON_EXTRACT(demo_bbox, '$')
        ))
    ),
    CONCAT('/images/demo/school_violation_', LPAD(n, 3, '0'), '.jpg'),
    CASE
        WHEN demo_status IN ('CONFIRMED', 'REJECTED') THEN DATE_ADD(demo_event_time, INTERVAL 2 HOUR)
        ELSE NULL
    END
FROM demo_rows;

SELECT
    COUNT(*) AS demo_violation_count,
    MIN(event_time) AS earliest_event_time,
    MAX(event_time) AS latest_event_time
FROM violation_event
WHERE id BETWEEN 100001 AND 100100
  AND remark LIKE '[DEMO_TEST_DATA]%';
