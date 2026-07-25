from pydantic import BaseModel, Field


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