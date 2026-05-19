from pydantic import BaseModel
from typing import List


class DetectItem(BaseModel):
    label: str
    confidence: float
    bbox: List[int]


class DetectResponse(BaseModel):
    message: str
    filename: str
    result: List[DetectItem]
