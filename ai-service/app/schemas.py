from pydantic import BaseModel, Field


class BoundingBox(BaseModel):
    x1: float
    y1: float
    x2: float
    y2: float


class DetectedObject(BaseModel):
    label: str
    confidence: float = Field(
        ge=0.0,
        le=1.0,
    )
    affectedPart: str = "UNKNOWN"
    boundingBox: BoundingBox

class DamageRecommendation(BaseModel):
    damageType: str
    recommendedAction: str
    partReplacementRequired: bool
    affectedParts: list[str] = Field(
        default_factory=list
    )

class DamageAnalysisResponse(BaseModel):
    damageSeverity: str
    affectedParts: list[str] = Field(
        default_factory=list
    )
    damageTypes: list[str] = Field(
        default_factory=list
    )

    repairRecommendations: list[
        DamageRecommendation
    ] = Field(
        default_factory=list
    )
    confidenceScore: float = Field(
        ge=0.0,
        le=1.0,
    )
    analysisMessage: str
    detections: list[DetectedObject] = Field(
        default_factory=list
    )