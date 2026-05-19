# 本地部署与启动说明

本文档说明当前项目各模块的本地启动方式。启动前请先阅读 `docs/README.md` 和 `docs/architecture.md`，确认模块职责和调用关系。

## 1. 环境要求

| 模块 | 依赖 |
| --- | --- |
| frontend | Node.js、npm |
| backend | JDK、Maven、Spring Boot 工程结构 |
| model-service | Python、pip |
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

当前 `backend` 目录仍是占位状态，尚未初始化完整 Spring Boot 工程，因此以下命令只有在后端工程补齐后才能运行：

```bash
cd backend
mvn spring-boot:run
```

后端初始化完成后，应补充：

- JDK 版本。
- Maven 版本。
- 服务端口。
- 数据库连接配置位置。
- 必需环境变量。
- 常见启动失败原因。

## 4. model-service

```bash
cd model-service
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

模型服务识别接口：

```text
POST http://localhost:8000/detect
```

如果后续使用真实 YOLOv8 权重，需要确认 `model-service/weights` 中的权重文件路径，并在模型服务代码或配置中同步说明。

## 5. database

1. 创建并连接 MySQL 8.0 实例。
2. 执行 `database/schema.sql`。
3. 执行 `database/init_data.sql`。

建议本地开发使用独立数据库，避免覆盖他人演示数据。

## 6. 推荐启动顺序

完整联调时建议按以下顺序启动：

1. 启动 MySQL，并确认数据库和初始化数据可用。
2. 启动 model-service，确认 `/detect` 可访问。
3. 启动 backend，确认能连接数据库和模型服务。
4. 启动 frontend，进行页面联调。

当前由于 `backend` 尚未初始化完整工程，只能先分别验证 `frontend`、`model-service` 和 `database`。

## 7. 联调检查项

```text
1. 前端页面是否能启动？
2. 数据库脚本是否能完整执行？
3. 模型服务 /detect 是否能返回识别结果？
4. 后端是否已初始化并能连接数据库？
5. 后端接口返回是否符合 docs/api.md？
6. 前端接口地址是否指向正确后端端口？
```
