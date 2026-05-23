USE traffic_platform;

-- Local test users for login verification.
-- Plaintext password for all rows below: Test@123456
-- Stored password_hash values are PBKDF2-HMAC-SHA256 hashes, not plaintext.

INSERT INTO user_info (id, account, password_hash, username, phone, status) VALUES
(9101, 'demo_user_01', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDE=$F8EbewShdZ2w/szckhRVL4TvaAzTigz4f8UeG78bwJ0=', '测试用户01', '13900009101', 'ACTIVE'),
(9102, 'demo_user_02', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDI=$/WHZaf4ME6FLs7WQCWsOgd46dwlp4O/LdfI9LopsqUE=', '测试用户02', '13900009102', 'ACTIVE'),
(9103, 'demo_user_03', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDM=$a3GAgMGKQJoPNA2JVTzx8gSqhWacJUNTFYj4JPUN2zI=', '测试用户03', '13900009103', 'ACTIVE'),
(9104, 'demo_user_04', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDQ=$+JCnw7R9h4Ny5x8jgR9M7nKIFUM0HzxnD7ceAxy78II=', '测试用户04', '13900009104', 'ACTIVE'),
(9105, 'demo_user_05', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDU=$5waCrFXpfzo85XVyiFBcxYsmjlWZtKkIZtE6/KFU51A=', '测试用户05', '13900009105', 'ACTIVE'),
(9106, 'demo_user_06', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDY=$iOGxxKzuccT8Oez0mKxRlmCTdWBhsU6QO3O2TxO4qW0=', '测试用户06', '13900009106', 'ACTIVE'),
(9107, 'demo_user_07', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDc=$HWI+zB8yH+ix3iDPz0Lz+w0Y+qlm44y7SVo/3wL09eE=', '测试用户07', '13900009107', 'ACTIVE'),
(9108, 'demo_user_08', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDg=$hChO1dyBnMQofUJ8OMRZUZiiOJrBbZsAnlgZ3Ad+0eU=', '测试用户08', '13900009108', 'ACTIVE'),
(9109, 'demo_user_09', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDk=$f00FW+MzXa+vO9RnpyZuE5BTKeVBsHwBaJK+jtA9yOw=', '测试用户09', '13900009109', 'ACTIVE'),
(9110, 'demo_user_10', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMTA=$JKhfQ0GJmK7o2a4MNyDYXX0RwKu+KGNFYT1b6rbhaT4=', '测试用户10', '13900009110', 'ACTIVE'),
(9111, 'demo_user_11', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMTE=$LBsJySGnBjIst72Cd2zyeu3iUPHFy53UUTnL/rCT37c=', '测试用户11', '13900009111', 'ACTIVE'),
(9112, 'demo_user_12', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMTI=$GeOWO1LfiZv5Fm9/T+0H/6f3K+PSp1tfb1n22P0h+T4=', '测试用户12', '13900009112', 'ACTIVE'),
(9113, 'demo_user_13', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMTM=$lYq+aUFQPhWGCCMtZug0hsMROA6mmMDnuxIk6GCrxVM=', '测试用户13', '13900009113', 'ACTIVE'),
(9114, 'demo_user_14', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMTQ=$0G5isanjZtHorrDqWS7E0//HI3sqjP9GtsYm3Ig94r8=', '测试用户14', '13900009114', 'ACTIVE'),
(9115, 'demo_user_15', 'pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMTU=$w9bWNctrsX0//UxgE6/bFZ460iSRu6iet01vUN2kkfA=', '测试用户15', '13900009115', 'ACTIVE')
ON DUPLICATE KEY UPDATE
account = VALUES(account),
password_hash = VALUES(password_hash),
username = VALUES(username),
phone = VALUES(phone),
status = VALUES(status);

UPDATE violation_event
SET user_id = 9101 + ((id - 100001) % 15)
WHERE id BETWEEN 100001 AND 100100
  AND remark LIKE '[DEMO_TEST_DATA]%';

SELECT id, account, username, phone, status
FROM user_info
WHERE id BETWEEN 9101 AND 9115
ORDER BY id;
