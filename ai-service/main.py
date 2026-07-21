from pathlib import Path

from fastapi import FastAPI, File, HTTPException, UploadFile

app = FastAPI(
    title="Vehicle Damage Analysis API",
    version="1.1.0",
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

    filename = Path(
        image.filename or "vehicle.jpg"
    ).name.lower()

    # Şimdilik gerçek AI modeli yerine deterministik demo sonucu.
    # Aynı dosya, her analizde aynı sonuca yakın bir çıktı üretir.
    score = sum(file_content[:1000]) + len(filename)
    result_type = score % 5

    if result_type == 0:
        damage_type = "SCRATCH"
        severity = "MINOR"
        vehicle_part = "FRONT_BUMPER"
        recommended_action = "POLISHING"
        part_replacement_required = False
        confidence_score = 0.87
        message = (
            "Ön tamponda yüzeysel çizik tespit edildi. "
            "Pasta-cila işlemi yeterli olabilir."
        )

    elif result_type == 1:
        damage_type = "PAINT_DAMAGE"
        severity = "MODERATE"
        vehicle_part = "FRONT_LEFT_DOOR"
        recommended_action = "FULL_PAINTING"
        part_replacement_required = False
        confidence_score = 0.89
        message = (
            "Sol ön kapıda orta seviyede boya hasarı tespit edildi. "
            "Parçanın boyanması önerilmektedir."
        )

    elif result_type == 2:
        damage_type = "DENT"
        severity = "MODERATE"
        vehicle_part = "FRONT_RIGHT_FENDER"
        recommended_action = "PAINTLESS_DENT_REPAIR"
        part_replacement_required = False
        confidence_score = 0.91
        message = (
            "Sağ ön çamurlukta orta seviyede göçük tespit edildi. "
            "Boyasız göçük düzeltme işlemi uygulanabilir."
        )

    elif result_type == 3:
        damage_type = "CRACK"
        severity = "SEVERE"
        vehicle_part = "FRONT_BUMPER"
        recommended_action = "PART_REPLACEMENT"
        part_replacement_required = True
        confidence_score = 0.94
        message = (
            "Ön tamponda ağır çatlak tespit edildi. "
            "Parça değişimi önerilmektedir."
        )

    else:
        damage_type = "NO_VISIBLE_DAMAGE"
        severity = "NONE"
        vehicle_part = "UNKNOWN"
        recommended_action = "NO_ACTION"
        part_replacement_required = False
        confidence_score = 0.82
        message = (
            "Belirgin bir araç hasarı tespit edilmedi. "
            "Herhangi bir onarım işlemi önerilmemektedir."
        )

    return {
        "damageType": damage_type,
        "damageSeverity": severity,
        "vehiclePart": vehicle_part,
        "recommendedAction": recommended_action,
        "partReplacementRequired": part_replacement_required,
        "confidenceScore": confidence_score,
        "analysisMessage": message,
    }