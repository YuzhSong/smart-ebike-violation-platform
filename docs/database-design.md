# 数据库设计说明

本文档面向数据库和后端负责人，说明当前版本 MySQL 表结构、字段含义、接口对应关系和入库规则。实际脚本以 `database/schema.sql` 为准，修改表结构时必须同步更新本文档。

## 1. 数据库基本信息

| 项 | 值 |
| --- | --- |
| 数据库类型 | MySQL 8.0 |
| 数据库名 | `traffic_platform` |
| 字符集 | `utf8mb4` |
| 初始化脚本 | `database/schema.sql`、`database/init_data.sql` |

## 2. 表结构总览

| 表名 | 用途 |
| --- | --- |
| `user_info` | 普通用户信息，用于用户端违法记录查询 |
| `admin_info` | 管理员信息，当前版本保留为演示数据 |
| `device_info` | 设备信息，用于设备上报和设备列表 |
| `violation_event` | 违法事件主表，用于列表、详情、审核、统计 |

## 3. user_info

保存普通用户信息。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 主键 |
| `account` | VARCHAR(64) | 登录账号，唯一 |
| `password_hash` | VARCHAR(255) | PBKDF2 密码哈希 |
| `username` | VARCHAR(64) | 用户名 |
| `phone` | VARCHAR(20) | 手机号，唯一 |
| `status` | VARCHAR(32) | 用户状态，例如 `ACTIVE` |
| `last_login_at` | DATETIME | 最近登录时间 |
| `updated_at` | DATETIME | 更新时间 |
| `created_at` | DATETIME | 创建时间 |

关联接口：

- `GET /api/user/violations?userId=1`
- `GET /api/user/violations/{id}`
- `GET /api/admin/violations/{id}`
- `POST /api/auth/login`
- `GET /api/admin/users`

## 4. admin_info

保存管理员信息。当前版本普通用户登录使用 `user_info`，`admin_info` 保留为演示数据。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 主键 |
| `admin_name` | VARCHAR(64) | 管理员名称 |
| `account` | VARCHAR(64) | 登录账号，唯一 |
| `password_hash` | VARCHAR(128) | 密码哈希或演示值 |
| `created_at` | DATETIME | 创建时间 |

## 5. device_info

保存设备基础信息。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 主键 |
| `device_code` | VARCHAR(64) | 设备编号，唯一 |
| `location_desc` | VARCHAR(255) | 安装位置描述 |
| `status` | VARCHAR(32) | 设备状态，例如 `ONLINE`、`OFFLINE` |
| `created_at` | DATETIME | 创建时间 |

关联接口：

- `POST /api/device/report`
- `GET /api/admin/devices`
- `GET /api/admin/violations`
- `GET /api/admin/violations/{id}`

## 6. violation_event

违法事件主表，是当前版本最核心的数据表。

### 6.1 当前字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 主键 |
| `user_id` | BIGINT | 用户 ID，可为空 |
| `device_id` | BIGINT | 设备 ID |
| `violation_type` | VARCHAR(128) | 违法类型 |
| `event_time` | DATETIME | 事件发生时间 |
| `status` | VARCHAR(32) | 事件状态 |
| `remark` | VARCHAR(255) NULL | 管理员审核备注 |
| `confidence` | DECIMAL(5,4) NULL | AI 识别置信度 |
| `bbox` | VARCHAR(255) NULL | 检测框，保存 JSON 字符串，例如 `[128,64,320,280]` |
| `model_result` | TEXT NULL | AI 服务原始返回结果，便于调试和演示 |
| `image_url` | VARCHAR(255) | 图片访问路径或相对路径 |
| `review_time` | DATETIME NULL | 管理员审核时间 |
| `updated_at` | DATETIME | 更新时间 |
| `created_at` | DATETIME | 创建时间 |

### 6.2 状态枚举

| 状态 | 说明 |
| --- | --- |
| `PENDING` | 待处理 |
| `REVIEWING` | 审核中 |
| `CONFIRMED` | 已确认违法 |
| `REJECTED` | 已驳回 |

### 6.3 违法类型

当前演示数据建议使用：

- `未佩戴头盔`
- `闯红灯`
- `逆行`

如新增违法类型，需要同步更新 `docs/api.md`。

## 7. 接口与表关系

| 接口 | 主要表 | 操作 |
| --- | --- | --- |
| `GET /api/user/violations` | `violation_event` | 按 `user_id` 查询 |
| `GET /api/user/violations/{id}` | `violation_event` | 按事件 ID 查询详情 |
| `GET /api/admin/violations` | `violation_event`、`device_info` | 分页、状态、设备筛选 |
| `GET /api/admin/violations/{id}` | `violation_event`、`user_info`、`device_info` | 查询完整详情 |
| `PUT /api/admin/violations/{id}/status` | `violation_event` | 更新 `status`、`remark`、`review_time` |
| `POST /api/device/report` | `device_info`、`violation_event` | 查询设备并新增违法事件 |
| `GET /api/admin/devices` | `device_info` | 查询设备列表 |
| `GET /api/admin/statistics` | `violation_event` | 聚合统计 |

## 8. 设备上报数据入库

设备上报后，后端应向 `violation_event` 写入：

| 字段 | 来源 |
| --- | --- |
| `user_id` | 当前版本可为空 |
| `device_id` | 根据 `deviceCode` 查询 `device_info.id` |
| `violation_type` | AI 服务返回的 `class_name` |
| `event_time` | 请求参数 `captureTime` |
| `status` | 默认 `PENDING` |
| `image_url` | 后端保存图片后的路径 |
| `confidence` | AI 服务返回的 `confidence` |
| `bbox` | AI 服务返回的 `bbox` JSON 字符串 |
| `model_result` | AI 服务完整返回 JSON |

## 9. 初始化数据要求

`database/init_data.sql` 应至少包含：

- 1 个普通用户。
- 1 个管理员。
- 2 个设备。
- 3 条违法事件，覆盖 `PENDING`、`REVIEWING`、`CONFIRMED` 等状态。

如果新增字段，应同步更新初始化数据。

## 10. 数据库验收

数据库负责人提交前至少验证：

```text
1. schema.sql 可以在空 MySQL 8.0 实例上执行成功。
2. init_data.sql 可以重复在新建库后执行成功。
3. 初始化后 user_info、device_info、violation_event 有演示数据。
4. violation_event 字段能支撑 docs/api.md 中的返回字段。
5. 后端能按 docs/backend-requirements.md 的接口需求完成查询、更新、统计。
```
