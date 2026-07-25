from pathlib import Path

from app.schemas import DamageAnalysisResponse


class DamageAnalyzer:
    """
    Araç hasar analizinin merkezi servisidir.

    Şu anda deterministik demo sonuçları üretir.
    Daha sonra bu sınıfın içine YOLO/OpenCV modeli bağlanacaktır.
    """

    def analyze(
        self,
        file_content: bytes,
        filename: str,
    ) -> DamageAnalysisResponse:
        safe_filename = Path(
            filename or "vehicle.jpg"
        ).name.lower()

        score = (
            sum(file_content[:1000])
            + len(safe_filename)
        )

        result_type = score % 5

        if result_type == 0:
            return DamageAnalysisResponse(
                damageType="SCRATCH",
                damageSeverity="MINOR",
                vehiclePart="FRONT_BUMPER",
                recommendedAction="POLISHING",
                partReplacementRequired=False,
                confidenceScore=0.87,
                analysisMessage=(
                    "Ön tamponda yüzeysel çizik tespit edildi. "
                    "Pasta-cila işlemi yeterli olabilir."
                ),
            )

        if result_type == 1:
            return DamageAnalysisResponse(
                damageType="PAINT_DAMAGE",
                damageSeverity="MODERATE",
                vehiclePart="FRONT_LEFT_DOOR",
                recommendedAction="FULL_PAINTING",
                partReplacementRequired=False,
                confidenceScore=0.89,
                analysisMessage=(
                    "Sol ön kapıda orta seviyede boya hasarı "
                    "tespit edildi. Parçanın boyanması "
                    "önerilmektedir."
                ),
            )

        if result_type == 2:
            return DamageAnalysisResponse(
                damageType="DENT",
                damageSeverity="MODERATE",
                vehiclePart="FRONT_RIGHT_FENDER",
                recommendedAction=(
                    "PAINTLESS_DENT_REPAIR"
                ),
                partReplacementRequired=False,
                confidenceScore=0.91,
                analysisMessage=(
                    "Sağ ön çamurlukta orta seviyede göçük "
                    "tespit edildi. Boyasız göçük düzeltme "
                    "işlemi uygulanabilir."
                ),
            )

        if result_type == 3:
            return DamageAnalysisResponse(
                damageType="CRACK",
                damageSeverity="SEVERE",
                vehiclePart="FRONT_BUMPER",
                recommendedAction="PART_REPLACEMENT",
                partReplacementRequired=True,
                confidenceScore=0.94,
                analysisMessage=(
                    "Ön tamponda ağır çatlak tespit edildi. "
                    "Parça değişimi önerilmektedir."
                ),
            )

        return DamageAnalysisResponse(
            damageType="NO_VISIBLE_DAMAGE",
            damageSeverity="NONE",
            vehiclePart="UNKNOWN",
            recommendedAction="NO_ACTION",
            partReplacementRequired=False,
            confidenceScore=0.82,
            analysisMessage=(
                "Belirgin bir araç hasarı tespit edilmedi. "
                "Herhangi bir onarım işlemi "
                "önerilmemektedir."
            ),
        )