# 后端开发需求与交付物

本文档面向后端负责人，说明当前版本后端需要实现的功能、工程结构、接口交付物和验收标准。接口字段以 `docs/api.md` 为准，数据表以 `docs/database-design.md` 和 `database/schema.sql` 为准。

## 1. 开发目标

后端需要完成一个可本地启动、可连接 MySQL、可供前端联调的 Spring Boot 服务，并打通以下核心链路：

```text
前端查询/审核
  -> 后端接口
  -> MySQL

设备上报图片
  -> 后端接收文件
  -> 调用 ai-service /detect/image
  -> 生成违法事件
  -> MySQL
```

## 2. 必须交付

后端交付物包括：

| 类型 | 交付物 | 验收标准 |
| --- | --- | --- |
| 工程骨架 | Spring Boot 工程、启动类、Maven 配置 | 在 `backend` 目录执行 `mvn spring-boot:run` 可启动 |
| 配置 | 服务端口、数据库连接、AI 服务地址、文件上传路径 | 配置项集中在配置文件中，能按本地环境修改 |
| 统一响应 | `ApiResponse` 或等价结构 | 返回格式符合 `docs/api.md` |
| 异常处理 | 参数错误、数据不存在、服务异常 | 错误码符合 `docs/api.md` 的通用约定 |
| 跨域配置 | 允许前端本地开发访问 | 前端 Vite 服务能调用后端接口 |
| 数据访问 | 用户、管理员、设备、违法事件表访问 | 能查询和更新 MySQL 数据 |
| AI 调用 | 调用 `http://127.0.0.1:8000/detect/image` | 设备上报时能获取识别结果或返回明确错误 |
| 文件上传 | 接收设备图片并保存路径 | 违法事件中能保存可展示的 `imageUrl` |
| 接口实现 | `docs/api.md` 中除 AI 推理服务外的后端接口 | 接口路径、方法、字段与文档一致 |
| 启动说明 | 更新 `docs/deployment.md` 中后端部分 | 说明 JDK、Maven、端口、数据库配置 |

## 3. 建议工程结构

可以按以下结构组织，允许根据实际 Spring Boot 习惯微调，但职责要清晰：

```text
backend/
  pom.xml
  src/main/java/.../
    Application.java
    common/
      ApiResponse.java
      GlobalExceptionHandler.java
    config/
      CorsConfig.java
      FileUploadProperties.java
      ModelServiceProperties.java
    controller/
      UserViolationController.java
      AdminViolationController.java
      DeviceReportController.java
      DeviceController.java
      StatisticsController.java
    service/
      ViolationService.java
      DeviceService.java
      StatisticsService.java
      ModelDetectService.java
    repository/ 或 mapper/
      UserMapper.java
      DeviceMapper.java
      ViolationMapper.java
    entity/
      UserInfo.java
      AdminInfo.java
      DeviceInfo.java
      ViolationEvent.java
    dto/
      request/
      response/
  src/main/resources/
    application.yml
```

## 4. 后端接口清单

必须实现以下接口：

| 接口 | 方法 | 路径 | 主要数据来源 |
| --- | --- | --- | --- |
| 查询用户违规列表 | `GET` | `/api/user/violations` | `violation_event` |
| 查询用户违规详情 | `GET` | `/api/user/violations/{id}` | `violation_event`、`device_info` |
| 管理员查询违规列表 | `GET` | `/api/admin/violations` | `violation_event` |
| 管理员查询违规详情 | `GET` | `/api/admin/violations/{id}` | `violation_event`、`user_info`、`device_info` |
| 更新违规状态 | `PUT` | `/api/admin/violations/{id}/status` | 更新 `violation_event` |
| 设备上报事件 | `POST` | `/api/device/report` | `device_info`、AI 推理服务、`violation_event` |
| 查询设备列表 | `GET` | `/api/admin/devices` | `device_info` |
| 查询统计数据 | `GET` | `/api/admin/statistics` | `violation_event` |

具体请求和返回字段以 `docs/api.md` 为准。

## 5. 设备上报落库规则

设备上报接口需要按以下规则处理：

1. 根据 `deviceCode` 查询 `device_info`。
2. 如果设备不存在，返回 `404` 或 `400`，不生成违法事件。
3. 保存上传图片，生成可访问或可记录的 `imageUrl`。
4. 调用 AI 推理服务 `/detect/image`，字段为 multipart `file`。
5. 如果模型返回至少一条识别结果，取第一条结果生成违法事件。
6. 违法事件初始状态为 `PENDING`。
7. `violation_type` 使用模型返回的 `label`。
8. `confidence`、`bbox`、`model_result` 按 `docs/database-design.md` 保存。
9. 当前版本设备上报不绑定具体用户，`user_id` 可以为空。
10. 如果 AI 推理服务不可用，返回明确错误，不写入半成品事件；如需改成“待识别事件”，必须先更新文档。

## 6. 图片保存规则

当前版本建议采用本地文件保存：

```text
backend/uploads/
```

数据库中只保存访问路径或相对路径，例如：

```text
/uploads/20260521_xxx.jpg
```

如果实现了静态资源映射，应在 `docs/deployment.md` 中说明访问方式。不要把图片二进制直接存入 MySQL。

## 7. 统计接口规则

统计接口至少返回：

- `totalViolations`：符合筛选条件的事件总数。
- `todayViolations`：当天事件数。
- `typeDistribution`：按 `violation_type` 分组计数。
- `statusDistribution`：按 `status` 分组计数。

如果前端需要趋势图，可补充 `trend` 字段；补充前需要同步更新 `docs/api.md`。

## 8. 验收方式

后端提交前至少完成以下验证：

```text
1. mvn spring-boot:run 能启动。
2. 能连接本地 MySQL traffic_platform。
3. GET /api/admin/devices 能返回初始化设备数据。
4. GET /api/user/violations?userId=1 能返回初始化违法数据。
5. PUT /api/admin/violations/{id}/status 能更新状态和备注。
6. POST /api/device/report 能接收图片并生成事件，或在 AI 推理服务未启动时返回明确错误。
7. GET /api/admin/statistics 能返回统计数据。
```

## 9. 不要求当前版本实现

以下能力不属于当前版本交付范围：

- 完整登录、注册、权限系统。
- 真实短信、支付、罚款流程。
- 生产级文件存储、对象存储、CDN。
- 复杂视频流处理。
- 多模型训练和模型管理平台。
