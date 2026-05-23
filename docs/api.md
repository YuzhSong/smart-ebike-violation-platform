# 接口约定文档

本文档是前端、后端、设备端和本地 AI 推理服务之间的接口契约。修改接口路径、请求方法、参数、返回字段或状态枚举时，必须同步更新本文档。

## 1. 通用约定

### 1.1 后端统一响应格式

除 AI 推理服务接口外，后端业务接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `code` | number | 是 | 业务状态码，成功为 `200` |
| `message` | string | 是 | 响应说明 |
| `data` | object/array/boolean/null | 否 | 响应数据 |

### 1.2 常用错误码

| code | 说明 |
| --- | --- |
| `200` | 成功 |
| `400` | 请求参数错误 |
| `401` | 未登录、无访问权限或账号密码错误 |
| `404` | 数据不存在 |
| `500` | 服务端异常 |

### 1.3 分页约定

分页查询默认使用 query 参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| `page` | number | 否 | `1` | 页码，从 1 开始 |
| `size` | number | 否 | `10` | 每页数量 |

分页返回格式：

```json
{
  "total": 3,
  "records": []
}
```

### 1.4 状态枚举

违法事件状态建议统一使用：

| 状态 | 说明 |
| --- | --- |
| `PENDING` | 待处理 |
| `REVIEWING` | 审核中 |
| `CONFIRMED` | 已确认违法 |
| `REJECTED` | 已驳回 |

违法类型当前演示数据建议使用：

| 类型 | 说明 |
| --- | --- |
| `未佩戴头盔` | 未佩戴安全头盔 |
| `闯红灯` | 违反信号灯通行 |
| `逆行` | 逆向行驶 |

## 2. 用户端接口

### 2.1 查询用户违规列表

- 用途：按用户查询违规记录列表。
- 请求方法：`GET`
- 路径：`/api/user/violations`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `userId` | query | number | 是 | 用户 ID |

请求示例：

```text
GET /api/user/violations?userId=1
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 101,
      "violationType": "未佩戴头盔",
      "eventTime": "2026-05-18 10:20:00",
      "status": "PENDING"
    }
  ]
}
```

### 2.2 查询违规详情

- 用途：查询单条违规事件详情。
- 请求方法：`GET`
- 路径：`/api/user/violations/{id}`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `id` | path | number | 是 | 违规事件 ID |

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 101,
    "userId": 1,
    "deviceId": 2,
    "violationType": "闯红灯",
    "eventTime": "2026-05-18 08:00:00",
    "status": "REVIEWING",
    "imageUrl": "/images/event_002.jpg"
  }
}
```

## 3. 管理员端接口

### 3.1 查询违规列表

- 用途：管理员分页或按条件查看违规事件。
- 请求方法：`GET`
- 路径：`/api/admin/violations`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `page` | query | number | 否 | 页码 |
| `size` | query | number | 否 | 每页数量 |
| `status` | query | string | 否 | 事件状态 |
| `deviceId` | query | number | 否 | 设备 ID |

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 3,
    "records": [
      {
        "id": 101,
        "violationType": "未佩戴头盔",
        "status": "PENDING"
      }
    ]
  }
}
```

### 3.2 查询违规详情

- 用途：管理员查看违规事件完整信息。
- 请求方法：`GET`
- 路径：`/api/admin/violations/{id}`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `id` | path | number | 是 | 违规事件 ID |

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 101,
    "userId": 1,
    "deviceId": 1,
    "violationType": "逆行",
    "eventTime": "2026-05-18 08:00:00",
    "status": "CONFIRMED",
    "imageUrl": "/images/event_001.jpg",
    "remark": "审核通过"
  }
}
```

### 3.3 更新违规状态

- 用途：管理员审核并更新事件状态。
- 请求方法：`PUT`
- 路径：`/api/admin/violations/{id}/status`
- Content-Type：`application/json`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `id` | path | number | 是 | 违规事件 ID |
| `status` | body | string | 是 | 新状态 |
| `remark` | body | string | 否 | 审核备注 |

请求示例：

```json
{
  "status": "CONFIRMED",
  "remark": "审核通过"
}
```

返回示例：

```json
{
  "code": 200,
  "message": "status updated",
  "data": true
}
```

## 4. 设备端接口

### 4.1 设备上报事件

- 用途：设备上传抓拍信息并触发识别。
- 请求方法：`POST`
- 路径：`/api/device/report`
- Content-Type：优先使用 `multipart/form-data`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `deviceCode` | form | string | 是 | 设备编号 |
| `captureTime` | form | string | 是 | 抓拍时间，格式 `YYYY-MM-DD HH:mm:ss` |
| `image` | form | file | 是 | 抓拍图片 |

返回示例：

```json
{
  "code": 200,
  "message": "report accepted",
  "data": {
    "eventId": 201
  }
}
```

## 5. AI 推理服务接口

### 5.1 图像识别

- 用途：调用本地 AI 推理服务进行违规行为识别。
- 请求方法：`POST`
- 路径：`http://127.0.0.1:8000/detect/image`
- Content-Type：`multipart/form-data`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `file` | form | file | 是 | 待识别图片 |

返回示例：

```json
{
  "success": true,
  "detections": [
    {
      "class_id": 2,
      "class_name": "no-helmet",
      "confidence": 0.93,
      "bbox": [128.0, 64.0, 320.0, 280.0]
    }
  ]
}
```

模型返回字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `class_id` | number | 模型类别 ID |
| `class_name` | string | 模型类别名称 |
| `confidence` | number | 置信度，范围 0 到 1 |
| `bbox` | number[] | 检测框，格式 `[x1, y1, x2, y2]` |

### 5.2 视频上传

- 用途：上传视频文件到本地 AI 推理服务。
- 请求方法：`POST`
- 路径：`http://127.0.0.1:8000/detect/video`
- Content-Type：`multipart/form-data`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `file` | form | file | 是 | 待处理视频 |

返回示例：

```json
{
  "success": true,
  "task_id": "b7d2c8f0e9a74c11b9dcb1e4a29c8a10",
  "status": "accepted",
  "filename": "sample.mp4",
  "input_path": "ai-service/uploads/b7d2c8f0e9a74c11b9dcb1e4a29c8a10.mp4",
  "message": "video uploaded; asynchronous processing can be added around this task_id"
}
```

## 6. 设备管理接口

### 6.1 查询设备列表

- 用途：管理员查看设备状态与安装点位。
- 请求方法：`GET`
- 路径：`/api/admin/devices`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `status` | query | string | 否 | 设备状态 |
| `keyword` | query | string | 否 | 搜索关键字 |

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "deviceCode": "DEV-001",
      "status": "ONLINE"
    },
    {
      "id": 2,
      "deviceCode": "DEV-002",
      "status": "ONLINE"
    }
  ]
}
```

## 7. 统计分析接口

### 7.1 查询统计数据

- 用途：管理员查看违规数量趋势、类型分布等。
- 请求方法：`GET`
- 路径：`/api/admin/statistics`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `startDate` | query | string | 否 | 开始日期，格式 `YYYY-MM-DD` |
| `endDate` | query | string | 否 | 结束日期，格式 `YYYY-MM-DD` |

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalViolations": 128,
    "todayViolations": 12,
    "typeDistribution": [
      {
        "type": "未佩戴头盔",
        "count": 70
      },
      {
        "type": "闯红灯",
        "count": 35
      },
      {
        "type": "逆行",
        "count": 23
      }
    ],
    "statusDistribution": [
      {
        "status": "PENDING",
        "count": 60
      },
      {
        "status": "REVIEWING",
        "count": 30
      },
      {
        "status": "CONFIRMED",
        "count": 38
      }
    ],
    "trend": [
      {
        "day": "05-18",
        "count": 12
      }
    ],
    "locationRanking": [
      {
        "name": "东城区路口A",
        "count": 23
      }
    ]
  }
}
```

返回字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `totalViolations` | number | 违规事件总数 |
| `todayViolations` | number | 当天违规事件数量 |
| `typeDistribution` | array | 按违法类型分组统计 |
| `statusDistribution` | array | 按事件状态分组统计 |
| `trend` | array | 最近 7 天趋势数据 |
| `locationRanking` | array | 按设备点位统计的排行数据 |

## 8. 登录与用户管理接口

### 8.1 用户登录

- 用途：普通用户登录。
- 请求方法：`POST`
- 路径：`/api/auth/login`
- Content-Type：`application/json`

请求示例：

```json
{
  "account": "demo_user_01",
  "password": "Test@123456"
}
```

返回示例：

```json
{
  "code": 200,
  "message": "login success",
  "data": {
    "userId": 9101,
    "account": "demo_user_01",
    "username": "测试用户01",
    "phone": "13900009101",
    "tokenType": "Bearer",
    "accessToken": "development-token"
  }
}
```

### 8.2 查询用户列表

- 用途：管理员查看用户列表和用户违法统计。
- 请求方法：`GET`
- 路径：`/api/admin/users`

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 9101,
      "account": "demo_user_01",
      "username": "测试用户01",
      "phone": "13900009101",
      "status": "ACTIVE",
      "violationCount": 3,
      "latestViolationTime": "2026-05-18 10:20:00",
      "lastLoginAt": "2026-05-23 10:00:00"
    }
  ]
}
```

## 9. 后端 AI 转发接口

### 9.1 图片识别转发

- 用途：前端或调试工具通过后端转发图片到本地 AI 推理服务。
- 请求方法：`POST`
- 路径：`/api/ai/detect/image`
- Content-Type：`multipart/form-data`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `file` | form | file | 是 | 待识别图片 |

返回数据为后端从 AI 服务第一条检测结果转换后的 `label`、`confidence`、`bbox`。

### 9.2 视频识别转发

- 用途：前端或调试工具通过后端转发视频到本地 AI 推理服务。
- 请求方法：`POST`
- 路径：`/api/ai/detect/video`
- Content-Type：`multipart/form-data`

请求参数：

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `file` | form | file | 是 | 待处理视频 |
