from pydantic import BaseModel, Field


class BoundingBox(BaseModel):
    x1: float
    y1: float
    x2: float
    y2: float


class DetectedObject(BaseModel):
    label: str
    confidence: float = Field(ge=0.0, le=1.0)
    boundingBox: BoundingBox


class DamageAnalysisResponse(BaseModel):
    damageType: str
    damageSeverity: str
    vehiclePart: str
    recommendedAction: str
    partReplacementRequired: bool
    confidenceScore: float = Field(
        ge=0.0,
        le=1.0,
    )
    analysisMessage: str
    detections: list[DetectedObject] = Field(
        default_factory=list
    )