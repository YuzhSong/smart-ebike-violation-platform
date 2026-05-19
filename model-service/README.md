# model-service 模块说明

该目录用于 Python 模型服务开发，后续接入 YOLOv8 进行违规行为识别。

## 本地启动（占位版）

```bash
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

## 当前接口

- `GET /health`：健康检查
- `POST /detect`：占位识别接口（返回模拟结果）
