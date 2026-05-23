import os
import shutil
import uuid
from io import BytesIO
from pathlib import Path
from typing import Any

from fastapi import FastAPI, File, HTTPException, UploadFile
from fastapi.responses import JSONResponse
from PIL import Image
import torch
from ultralytics import YOLO


CLASS_NAMES = {
    0: "Cyclists",
    1: "helmet",
    2: "no-helmet",
    3: "License-plate",
    4: "modification",
}

BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = Path(r"D:\Code\detection\yolov8\weights\best.pt")
UPLOAD_DIR = Path(os.getenv("UPLOAD_DIR", str(BASE_DIR / "uploads"))).expanduser()
OUTPUT_DIR = Path(os.getenv("OUTPUT_DIR", str(BASE_DIR / "outputs"))).expanduser()

app = FastAPI(
    title="Smart E-bike Violation AI Service",
    version="0.1.0",
    description="YOLOv8 inference service for non-motor vehicle violation detection.",
)

model: YOLO | None = None
model_error: str | None = None
_original_torch_load = torch.load


def _allow_trusted_checkpoint_loading() -> None:
    # YOLOv8 .pt files are PyTorch checkpoints. PyTorch 2.6+ defaults to
    # weights_only=True, which blocks older trusted Ultralytics checkpoints.
    def torch_load_compat(*args: Any, **kwargs: Any) -> Any:
        kwargs.setdefault("weights_only", False)
        return _original_torch_load(*args, **kwargs)

    torch.load = torch_load_compat


@app.on_event("startup")
def load_model() -> None:
    global model, model_error
    if not MODEL_PATH.exists():
        model = None
        model_error = f"model file not found: {MODEL_PATH}"
        return

    try:
        _allow_trusted_checkpoint_loading()
        model = YOLO(str(MODEL_PATH))
        model_error = None
    except Exception as exc:  # pragma: no cover - depends on local torch/ultralytics install
        model = None
        model_error = str(exc)


@app.get("/health")
def health() -> dict[str, Any]:
    return {
        "status": "ok" if model is not None else "degraded",
        "model_loaded": model is not None,
        "model_path": str(MODEL_PATH),
        "error": model_error,
    }


@app.post("/detect/image")
async def detect_image(file: UploadFile = File(...)) -> dict[str, Any]:
    if model is None:
        raise HTTPException(status_code=503, detail=model_error or "model is not loaded")

    contents = await file.read()
    if not contents:
        raise HTTPException(status_code=400, detail="uploaded image is empty")

    try:
        image = Image.open(BytesIO(contents)).convert("RGB")
        results = model(image)
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"inference failed: {exc}") from exc

    detections = []
    for result in results:
        boxes = result.boxes
        if boxes is None:
            continue
        for box in boxes:
            class_id = int(box.cls.item())
            xyxy = [float(value) for value in box.xyxy[0].tolist()]
            detections.append(
                {
                    "class_id": class_id,
                    "class_name": CLASS_NAMES.get(class_id, str(class_id)),
                    "confidence": round(float(box.conf.item()), 6),
                    "bbox": xyxy,
                }
            )

    return {"success": True, "detections": detections}


@app.post("/detect/video")
async def detect_video(file: UploadFile = File(...)) -> JSONResponse:
    if not file.filename:
        raise HTTPException(status_code=400, detail="video filename is required")

    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    suffix = Path(file.filename).suffix or ".mp4"
    task_id = uuid.uuid4().hex
    stored_path = UPLOAD_DIR / f"{task_id}{suffix}"

    with stored_path.open("wb") as target:
        shutil.copyfileobj(file.file, target)

    return JSONResponse(
        status_code=202,
        content={
            "success": True,
            "task_id": task_id,
            "status": "accepted",
            "filename": file.filename,
            "input_path": str(stored_path),
            "message": "video uploaded; asynchronous processing can be added around this task_id",
        },
    )
