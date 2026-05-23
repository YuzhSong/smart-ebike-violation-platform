# database 模块说明

- 数据库版本：`MySQL 8.0`
- 建议数据库名：`traffic_platform`

## 初始化步骤

1. 先执行 `schema.sql` 创建数据库与表结构；
2. 再执行 `init_data.sql` 插入最小初始化数据；
3. 本地开发需要较多演示数据时，再执行 `seed_demo_violations.sql`。
4. 本地开发需要登录测试账号时，执行 `upgrade_20260523_user_login_fields.sql` 后再执行 `seed_test_users.sql`。

## 演示数据说明

`seed_demo_violations.sql` 会插入约 100 条学校附近电动车违章测试记录，时间范围覆盖 `2026-02-01` 到 `2026-05-23`。

为避免和最终上线后的真实数据混在一起，脚本固定使用以下演示数据 ID 段：

- `device_info.id`: `9001-9008`
- `violation_event.id`: `100001-100100`

所有演示违章记录的 `remark` 都以 `[DEMO_TEST_DATA]` 开头。上线前如需清理，可执行：

```sql
DELETE FROM violation_event
WHERE id BETWEEN 100001 AND 100100
  AND remark LIKE '[DEMO_TEST_DATA]%';

DELETE FROM device_info
WHERE id BETWEEN 9001 AND 9008
  AND device_code LIKE 'DEMO-%';

```

## 登录测试用户

`seed_test_users.sql` 会插入 15 个普通用户测试账号，ID 范围为 `9101-9115`。

测试账号统一使用：

- 账号：`demo_user_01` 到 `demo_user_15`
- 明文密码：`Test@123456`

数据库中不保存明文密码，`password_hash` 保存 PBKDF2-HMAC-SHA256 哈希。脚本还会把 `100001-100100` 的演示违章记录分配给这些测试用户，方便登录后查看“我的违法记录”。
