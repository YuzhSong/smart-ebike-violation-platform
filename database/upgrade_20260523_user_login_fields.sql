USE traffic_platform;

-- Upgrade user_info for real login support.
-- Password hashes use: pbkdf2_sha256$iterations$base64(salt)$base64(hash)

DROP PROCEDURE IF EXISTS add_user_info_column_if_missing;
DROP PROCEDURE IF EXISTS add_user_info_index_if_missing;

DELIMITER //

CREATE PROCEDURE add_user_info_column_if_missing(
    IN p_column_name VARCHAR(64),
    IN p_column_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'user_info'
          AND COLUMN_NAME = p_column_name
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE user_info ADD COLUMN ', p_column_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

CREATE PROCEDURE add_user_info_index_if_missing(
    IN p_index_name VARCHAR(64),
    IN p_index_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'user_info'
          AND INDEX_NAME = p_index_name
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE user_info ADD ', p_index_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

DELIMITER ;

CALL add_user_info_column_if_missing('account', 'account VARCHAR(64) NULL AFTER id');
CALL add_user_info_column_if_missing('password_hash', 'password_hash VARCHAR(255) NULL AFTER account');
CALL add_user_info_column_if_missing('status', 'status VARCHAR(32) NOT NULL DEFAULT ''ACTIVE'' AFTER phone');
CALL add_user_info_column_if_missing('last_login_at', 'last_login_at DATETIME NULL AFTER status');
CALL add_user_info_column_if_missing('updated_at', 'updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER last_login_at');

UPDATE user_info
SET account = username
WHERE account IS NULL OR account = '';

UPDATE user_info
SET password_hash = 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDE=$F8EbewShdZ2w/szckhRVL4TvaAzTigz4f8UeG78bwJ0='
WHERE password_hash IS NULL OR password_hash = '';

ALTER TABLE user_info
    MODIFY account VARCHAR(64) NOT NULL,
    MODIFY password_hash VARCHAR(255) NOT NULL,
    MODIFY phone VARCHAR(20) NOT NULL,
    MODIFY status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE';

CALL add_user_info_index_if_missing('uk_user_info_account', 'UNIQUE INDEX uk_user_info_account (account)');
CALL add_user_info_index_if_missing('uk_user_info_phone', 'UNIQUE INDEX uk_user_info_phone (phone)');

DROP PROCEDURE add_user_info_column_if_missing;
DROP PROCEDURE add_user_info_index_if_missing;
