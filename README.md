# 非机动车智能违法监管与服务平台

英文仓库名：`smart-ebike-violation-platform`

## 项目简介

本项目面向非机动车违法治理场景，构建集前端用户服务、后台监管管理、数据库存储和 YOLOv8 智能识别于一体的智慧交通平台原型。

## 系统模块

- `frontend`：Vue3 + Vite 前端。
- `backend`：Spring Boot 后端，提供业务接口、数据库访问、文件上传和 AI 服务调用。
- `ai-service`：本地 FastAPI + YOLOv8 推理服务。
- `database`：MySQL 建表与初始化数据。
- `docs`：产品说明、接口契约、协作规范和部署说明。

## 文档入口

开发前请先阅读：

1. `docs/README.md`：文档入口和阅读顺序。
2. `docs/collaboration.md`：协作边界和修改规则。
3. `docs/product.md`：产品目标、用户角色和核心流程。
4. `docs/architecture.md`：模块职责和调用链路。
5. `docs/api.md`：接口契约。
6. `docs/backend-requirements.md`：后端开发需求、接口实现和验收标准。
7. `docs/database-design.md`：数据库表结构、字段含义和入库规则。
8. `docs/deployment.md`：本地启动和联调说明。

使用 Codex 辅助开发时，请阅读 `docs/ai-codex-prompt.md`。

## 当前状态

- 前端目录已存在，可按 `docs/deployment.md` 启动。
- 后端 Spring Boot 工程已初始化，可按 `docs/deployment.md` 启动。
- AI 推理服务在 `ai-service` 中实现，默认本地地址为 `http://127.0.0.1:8000`。
- 数据库脚本已存在，可按 `database/README.md` 和 `docs/deployment.md` 初始化。
