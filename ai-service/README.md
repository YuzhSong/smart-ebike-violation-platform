# AI Inference Service

FastAPI service for the YOLOv8 non-motor vehicle violation model.

## Local setup

```bash
cd ai-service
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
```

`ultralytics==8.0.133` is pinned because the local `best.pt` checkpoint was produced with that Ultralytics package layout.

Place the trained model manually at:

```text
ai-service/models/best.pt
```

Do not commit model files. They are large binary artifacts and should be distributed through a model registry, release asset, or local deployment process.

## Configuration

The service reads the model path from `MODEL_PATH`.

```bash
set MODEL_PATH=models/best.pt
```

If `MODEL_PATH` is not set, the default is `ai-service/models/best.pt`.

## Start

```bash
uvicorn app:app --host 127.0.0.1 --port 8000
```

Health check:

```bash
curl http://127.0.0.1:8000/health
```

Image detection:

```bash
curl -X POST http://127.0.0.1:8000/detect/image -F "file=@sample.jpg"
```

Video upload:

```bash
curl -X POST http://127.0.0.1:8000/detect/video -F "file=@sample.mp4"
```

`/detect/video` currently stores the uploaded video under `uploads/` and returns a `task_id`. A later extension can add a background worker, task status endpoint, and output video path under `outputs/`.

## Backend integration

The Spring Boot backend should call this service over HTTP. Configure the backend with:

```bash
set AI_SERVICE_URL=http://127.0.0.1:8000
```

The backend calls:

```text
POST http://127.0.0.1:8000/detect/image
POST http://127.0.0.1:8000/detect/video
```

The model service returns image detections in this shape:

```json
{
  "success": true,
  "detections": [
    {
      "class_id": 0,
      "class_name": "Cyclists",
      "confidence": 0.91,
      "bbox": [10.0, 20.0, 120.0, 220.0]
    }
  ]
}
```
