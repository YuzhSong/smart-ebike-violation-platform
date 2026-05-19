from fastapi import FastAPI, UploadFile, File

app = FastAPI(title="模型服务（占位版）", version="0.1.0")


@app.get("/health")
def health():
    return {"status": "ok", "service": "model-service"}


@app.post("/detect")
async def detect(file: UploadFile = File(...)):
    # 中文注释：当前先返回模拟识别结果，后续在 detector.py 中接入 YOLOv8 实际推理逻辑。
    return {
        "message": "mock detect success",
        "filename": file.filename,
        "result": [
            {
                "label": "未佩戴头盔",
                "confidence": 0.93,
                "bbox": [128, 64, 320, 280]
            }
        ]
    }
