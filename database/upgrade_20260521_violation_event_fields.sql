USE traffic_platform;

-- 从早期 violation_event 表结构升级到当前后端实体需要的字段。
-- 本脚本可重复执行：字段已存在时不会再次 ALTER TABLE。

DELIMITER //

CREATE PROCEDURE add_violation_event_column_if_missing(
    IN column_name VARCHAR(64),
    IN column_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'violation_event'
          AND COLUMN_NAME = column_name
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE violation_event ADD COLUMN ', column_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

DELIMITER ;

CALL add_violation_event_column_if_missing('remark', 'remark VARCHAR(255) NULL AFTER status');
CALL add_violation_event_column_if_missing('confidence', 'confidence DECIMAL(5,4) NULL AFTER remark');
CALL add_violation_event_column_if_missing('bbox', 'bbox VARCHAR(255) NULL AFTER confidence');
CALL add_violation_event_column_if_missing('model_result', 'model_result TEXT NULL AFTER bbox');
CALL add_violation_event_column_if_missing('review_time', 'review_time DATETIME NULL AFTER image_url');
CALL add_violation_event_column_if_missing('updated_at', 'updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER review_time');

DROP PROCEDURE add_violation_event_column_if_missing;

UPDATE violation_event
SET remark = '审核通过', review_time = NOW()
WHERE status = 'CONFIRMED' AND remark IS NULL;
