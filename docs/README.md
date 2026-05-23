# 项目文档入口

本目录用于统一项目背景、接口契约、协作边界和本地启动方式。所有开发任务开始前，应先从本文档确认阅读顺序，避免只看单个文件导致理解不完整。

## 1. 必读文档

无论负责前端、后端、AI 推理服务还是数据库，开发前都必须先阅读：

1. `README.md`：项目总体目标和模块划分。
2. `docs/README.md`：文档入口和阅读顺序。
3. `docs/collaboration.md`：成员分工、可修改范围、跨边界修改流程。

## 2. 按任务阅读

- 涉及业务功能、页面流程、用户角色：阅读 `docs/product.md`。
- 涉及模块职责、调用链路、前后端联调：阅读 `docs/architecture.md`。
- 涉及接口新增、修改、联调：阅读 `docs/api.md`。
- 涉及后端工程、接口实现、AI 推理服务调用：阅读 `docs/backend-requirements.md`。
- 涉及数据库表结构、初始化数据：阅读 `docs/database-design.md`、`database/README.md`、`database/schema.sql`、`database/init_data.sql`。
- 涉及本地启动、环境配置、部署演示：阅读 `docs/deployment.md`。
- 使用 Codex 辅助开发：阅读 `docs/ai-codex-prompt.md`。
- 安排阶段任务或验收进度：阅读 `docs/development-plan.md`。

## 3. 后端/数据库负责人阅读顺序

后端和数据库负责人开发前建议按以下顺序阅读：

1. `README.md`
2. `docs/README.md`
3. `docs/collaboration.md`
4. `docs/product.md`
5. `docs/architecture.md`
6. `docs/api.md`
7. `docs/backend-requirements.md`
8. `docs/database-design.md`
9. `docs/deployment.md`
10. `docs/development-plan.md`

## 4. 文档维护规则

- 修改接口、数据结构、启动方式或模块职责时，必须同步更新对应文档。
- 修改 `docs`、`README.md`、`.gitignore`、`database` 等公共内容前，需要先确认影响范围。
- 文档中出现“当前版本”“后续”等状态描述时，应尽量写清楚当前是否可运行、是否已实现、是否只是计划。
- 每次较大文档调整后，应在提交说明中列出影响的文档和调整原因。

## 5. 推荐开发前检查

```text
1. 我是否读过 README.md、docs/README.md、docs/collaboration.md？
2. 我是否明确本次任务允许修改哪些目录？
3. 如果涉及接口，我是否核对了 docs/api.md？
4. 如果涉及启动或联调，我是否核对了 docs/deployment.md？
5. 如果需要跨目录修改，我是否先说明原因并取得确认？
```
