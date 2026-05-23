# 非机动车智能违法监管与服务平台

英文仓库名：`smart-ebike-violation-platform`

## 项目简介

本项目面向非机动车违法治理场景，包含前端用户服务、后端监管管理、数据库存储和 YOLOv8 智能识别服务。

## 系统模块

- `frontend`：Vue 3 + Vite 前端。
- `backend`：Spring Boot 后端，提供业务接口、数据库访问、文件上传和 AI 服务调用。
- `ai-service`：FastAPI + YOLOv8 本地推理服务。
- `database`：MySQL 建表与初始化数据。
- `docs`：产品说明、接口契约、协作规范和部署说明。

## 本地运行

建议按下面顺序分别打开终端运行。

### 1. 启动 MySQL

确保本机 MySQL 已启动，并且存在数据库 `traffic_platform`。

如果还没有初始化数据库，先执行：

```sql
source database/schema.sql;
source database/init_data.sql;
```

当前后端默认数据库配置：

- 地址：`jdbc:mysql://localhost:3306/traffic_platform`
- 用户名：`root`
- 密码：`0623`

### 2. 启动 AI 服务

```powershell
cd D:\Code\Dachuang\ai-service
.\.venv\Scripts\activate
uvicorn app:app --host 127.0.0.1 --port 8000
```

模型路径已在代码中固定为：

```text
D:\Code\detection\yolov8\weights\best.pt
```

检查 AI 服务：

```powershell
Invoke-RestMethod http://127.0.0.1:8000/health
```

### 3. 启动后端

```powershell
cd D:\Code\Dachuang\backend
.\mvnw.cmd spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```

### 4. 启动前端

```powershell
cd D:\Code\Dachuang\frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

## License

`LICENSE` 是开源许可证文件。本项目当前使用 MIT License，意思是别人可以在保留许可证和版权声明的前提下使用、复制、修改和分发代码，同时作者不承担使用该软件产生的问题责任。

## 文档入口

- `docs/README.md`：文档入口和阅读顺序。
- `docs/product.md`：产品目标、用户角色和核心流程。
- `docs/architecture.md`：模块职责和调用链路。
- `docs/api.md`：接口契约。
- `docs/deployment.md`：本地启动和联调说明。
