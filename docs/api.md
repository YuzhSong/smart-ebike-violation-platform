# 接口约定文档（当前版本）

## 用户端

### 1) 查询用户违规列表
- 用途：按用户查询违规记录列表。
- 请求方法：`GET`
- 路径：`/api/user/violations?userId=1`
- 请求参数：
  - `userId`（query，必填，用户 ID）
- 返回示例：
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

### 2) 查询违规详情
- 用途：查询单条违规事件详情。
- 请求方法：`GET`
- 路径：`/api/user/violations/{id}`
- 请求参数：
  - `id`（path，必填，违规事件 ID）
- 返回示例：
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

## 管理员端

### 3) 查询违规列表
- 用途：管理员分页/条件查看违规事件。
- 请求方法：`GET`
- 路径：`/api/admin/violations`
- 请求参数：可扩展 `page`、`size`、`status`、`deviceId`（query）
- 返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 3,
    "records": [
      { "id": 101, "violationType": "未佩戴头盔", "status": "PENDING" }
    ]
  }
}
```

### 4) 查询违规详情
- 用途：管理员查看违规事件完整信息。
- 请求方法：`GET`
- 路径：`/api/admin/violations/{id}`
- 请求参数：
  - `id`（path，必填）
- 返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 101,
    "deviceId": 1,
    "violationType": "逆行",
    "status": "CONFIRMED"
  }
}
```

### 5) 更新违规状态
- 用途：管理员审核并更新事件状态。
- 请求方法：`PUT`
- 路径：`/api/admin/violations/{id}/status`
- 请求参数：
  - `id`（path，必填）
  - Body 示例：`{"status":"CONFIRMED","remark":"审核通过"}`
- 返回示例：
```json
{
  "code": 200,
  "message": "status updated",
  "data": true
}
```

## 设备端

### 6) 设备上报事件
- 用途：设备上传抓拍信息并触发识别。
- 请求方法：`POST`
- 路径：`/api/device/report`
- 请求参数：
  - `deviceCode`（form 或 json）
  - `captureTime`（form 或 json）
  - `image`（multipart 文件）
- 返回示例：
```json
{
  "code": 200,
  "message": "report accepted",
  "data": {
    "eventId": 201
  }
}
```

## 模型服务

### 7) 图像识别
- 用途：调用模型服务进行违规行为识别。
- 请求方法：`POST`
- 路径：`http://localhost:8000/detect`
- 请求参数：
  - `file`（multipart 文件）
- 返回示例：
```json
{
  "message": "mock detect success",
  "filename": "capture.jpg",
  "result": [
    {
      "label": "未佩戴头盔",
      "confidence": 0.93,
      "bbox": [128, 64, 320, 280]
    }
  ]
}
```

## 设备管理

### 8) 查询设备列表
- 用途：管理员查看设备状态与安装点位。
- 请求方法：`GET`
- 路径：`/api/admin/devices`
- 请求参数：可扩展 `status`、`keyword`（query）
- 返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "deviceCode": "DEV-001", "status": "ONLINE" },
    { "id": 2, "deviceCode": "DEV-002", "status": "ONLINE" }
  ]
}
```

## 统计分析

### 9) 查询统计数据
- 用途：管理员查看违规数量趋势、类型分布等。
- 请求方法：`GET`
- 路径：`/api/admin/statistics`
- 请求参数：可扩展 `startDate`、`endDate`（query）
- 返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalViolations": 128,
    "todayViolations": 12,
    "typeDistribution": [
      { "type": "未佩戴头盔", "count": 70 },
      { "type": "闯红灯", "count": 35 },
      { "type": "逆行", "count": 23 }
    ]
  }
}
```
