# 本地部署与启动说明

## frontend

```bash
cd frontend
npm install
npm run dev
```

## backend（后续）

```bash
cd backend
mvn spring-boot:run
```

## model-service

```bash
cd model-service
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

## database

1. 创建并连接 MySQL 8.0 实例；
2. 执行 `database/schema.sql`；
3. 执行 `database/init_data.sql`。
