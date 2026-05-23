# 本地部署与启动说明

本文档说明当前项目各模块的本地启动方式。启动前请先阅读 `docs/README.md` 和 `docs/architecture.md`，确认模块职责和调用关系。

## 1. 环境要求

| 模块 | 依赖 |
| --- | --- |
| frontend | Node.js、npm |
| backend | JDK、Maven、Spring Boot 工程结构 |
| ai-service | Python、pip、YOLOv8 权重文件 |
| database | MySQL 8.0 |

## 2. frontend

当前 `frontend` 已存在 Vite 项目结构。

```bash
cd frontend
npm install
npm run dev
```

默认启动后按终端输出访问本地地址，通常为：

```text
http://localhost:5173
```

## 3. backend

当前 `backend` 已初始化为 Spring Boot 工程。默认端口为 `8080`，数据库和 AI 服务地址可通过环境变量覆盖。

```bash
cd backend
mvn spring-boot:run
```

常用配置项：

- `SERVER_PORT`：后端端口，默认 `8080`。
- `DB_URL`：MySQL 连接地址，默认连接 `traffic_platform`。
- `DB_USERNAME`：数据库用户名，默认 `root`。
- `DB_PASSWORD`：数据库密码，默认 `123456`。
- `UPLOAD_DIR`：设备图片保存目录，默认 `uploads`。
- `AI_SERVICE_URL`：本地 AI 推理服务地址，默认 `http://127.0.0.1:8000`。

后端具体交付物和验收方式见 `docs/backend-requirements.md`。

## 4. ai-service

```bash
cd ai-service
pip install -r requirements.txt
uvicorn app:app --host 127.0.0.1 --port 8000
```

AI 推理服务接口：

```text
GET  http://127.0.0.1:8000/health
POST http://127.0.0.1:8000/detect/image
POST http://127.0.0.1:8000/detect/video
```

模型权重默认放在：

```text
ai-service/models/best.pt
```

如果权重放在其他本地路径，可以通过 `MODEL_PATH` 指定。模型文件不提交到仓库。

## 5. database

1. 创建并连接 MySQL 8.0 实例。
2. 执行 `database/schema.sql`。
3. 执行 `database/init_data.sql`。

建议本地开发使用独立数据库，避免覆盖他人演示数据。

## 6. 推荐启动顺序

完整联调时建议按以下顺序启动：

1. 启动 MySQL，并确认数据库和初始化数据可用。
2. 启动 ai-service，确认 `/health` 和 `/detect/image` 可访问。
3. 启动 backend，确认能连接数据库和 AI 推理服务。
4. 启动 frontend，进行页面联调。

## 7. 联调检查项

```text
1. 前端页面是否能启动？
2. 数据库脚本是否能完整执行？
3. AI 推理服务 /detect/image 是否能返回识别结果？
4. 后端是否能连接数据库？
5. 后端接口返回是否符合 docs/api.md？
6. 前端接口地址是否指向正确后端端口？
```
