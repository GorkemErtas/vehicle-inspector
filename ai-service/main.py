from decimal import Decimal
from pathlib import Path

from fastapi import FastAPI, File, HTTPException, UploadFile

app = FastAPI(
    title="Vehicle Damage Analysis API",
    version="1.0.0",
)

ALLOWED_CONTENT_TYPES = {
    "image/jpeg",
    "image/png",
    "image/webp",
}


@app.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "vehicle-damage-ai",
    }


@app.post("/api/v1/analyze")
async def analyze_damage(image: UploadFile = File(...)):
    if image.content_type not in ALLOWED_CONTENT_TYPES:
        raise HTTPException(
            status_code=400,
            detail="Only JPG, PNG and WEBP images are supported.",
        )

    file_content = await image.read()

    if not file_content:
        raise HTTPException(
            status_code=400,
            detail="Uploaded image cannot be empty.",
        )

    filename = Path(image.filename or "vehicle.jpg").name.lower()

    # Şimdilik gerçek model yerine deterministik demo sonucu.
    # Aynı dosya adı her analizde benzer sonuç üretir.
    score = sum(file_content[:1000]) + len(filename)
    result_type = score % 4

    if result_type == 0:
        damage_type = "SCRATCH"
        severity = "MINOR"
        confidence_score = 0.87
        estimated_cost = Decimal("3500.00")
        message = "Araç yüzeyinde hafif çizik tespit edildi."

    elif result_type == 1:
        damage_type = "DENT"
        severity = "MODERATE"
        confidence_score = 0.91
        estimated_cost = Decimal("8500.00")
        message = "Kaporta üzerinde orta seviyede göçük tespit edildi."

    elif result_type == 2:
        damage_type = "BUMPER_DAMAGE"
        severity = "SEVERE"
        confidence_score = 0.94
        estimated_cost = Decimal("18000.00")
        message = "Tampon bölgesinde ciddi hasar tespit edildi."

    else:
        damage_type = "NO_VISIBLE_DAMAGE"
        severity = "NONE"
        confidence_score = 0.82
        estimated_cost = Decimal("0.00")
        message = "Belirgin bir araç hasarı tespit edilmedi."

    return {
        "damageType": damage_type,
        "damageSeverity": severity,
        "confidenceScore": confidence_score,
        "estimatedCost": estimated_cost,
        "analysisMessage": message,
    }