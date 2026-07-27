from io import BytesIO
from pathlib import Path
from typing import Any

from PIL import Image, UnidentifiedImageError
from ultralytics import YOLO

from app.schemas import (
    BoundingBox,
    DamageAnalysisResponse,
    DetectedObject,
)


PROJECT_ROOT = Path(__file__).resolve().parent.parent

DEFAULT_VEHICLE_MODEL_PATH = PROJECT_ROOT / "yolo11n.pt"
DEFAULT_DAMAGE_MODEL_PATH = PROJECT_ROOT / "models" / "best.pt"


class DamageAnalyzer:
    """
    Araç ve hasar tespiti yapan analiz servisi.

    Genel YOLO modeli araç tespiti için,
    özel eğitilmiş YOLO modeli ise araç üzerindeki
    hasarlı bölgeleri tespit etmek için kullanılır.
    """

    VEHICLE_LABELS = {
        "car",
        "truck",
        "bus",
        "motorcycle",
    }

    def __init__(
        self,
        vehicle_model_path: str | Path = DEFAULT_VEHICLE_MODEL_PATH,
        damage_model_path: str | Path = DEFAULT_DAMAGE_MODEL_PATH,
        vehicle_confidence_threshold: float = 0.40,
        damage_confidence_threshold: float = 0.08,
        damage_iou_threshold: float = 0.20,
    ) -> None:
        self.vehicle_model_path = Path(vehicle_model_path)
        self.damage_model_path = Path(damage_model_path)

        self.vehicle_confidence_threshold = (
            vehicle_confidence_threshold
        )
        self.damage_confidence_threshold = (
            damage_confidence_threshold
        )
        self.damage_iou_threshold = damage_iou_threshold

        if not self.vehicle_model_path.exists():
            raise FileNotFoundError(
                "Vehicle detection model was not found: "
                f"{self.vehicle_model_path}"
            )

        if not self.damage_model_path.exists():
            raise FileNotFoundError(
                "Damage detection model was not found: "
                f"{self.damage_model_path}"
            )

        self.vehicle_model = YOLO(
            str(self.vehicle_model_path)
        )

        self.damage_model = YOLO(
            str(self.damage_model_path)
        )

    def analyze(
        self,
        file_content: bytes,
        filename: str,
    ) -> DamageAnalysisResponse:
        safe_filename = Path(
            filename or "vehicle.jpg"
        ).name.lower()

        image = self._load_image(file_content)

        vehicle_results = self.vehicle_model.predict(
            source=image,
            conf=self.vehicle_confidence_threshold,
            verbose=False,
        )

        damage_results = self.damage_model.predict(
            source=image,
            conf=self.damage_confidence_threshold,
            iou=self.damage_iou_threshold,
            max_det=10,
            verbose=False,
        )

        vehicle_detections = self._extract_detections(
            vehicle_results
        )

        vehicle_detections = [
            detection
            for detection in vehicle_detections
            if detection.label in self.VEHICLE_LABELS
        ]

        damage_detections = self._extract_detections(
            damage_results
        )

        if not damage_detections:
            return self._build_no_damage_response(
                filename=safe_filename,
                vehicle_detections=vehicle_detections,
            )

        primary_damage = max(
            damage_detections,
            key=lambda detection: detection.confidence,
        )

        return self._build_damage_response(
            filename=safe_filename,
            primary_damage=primary_damage,
            damage_detections=damage_detections,
        )

    def _build_damage_response(
        self,
        filename: str,
        primary_damage: DetectedObject,
        damage_detections: list[DetectedObject],
    ) -> DamageAnalysisResponse:
        damage_type = primary_damage.label.upper()

        recommended_action = (
            self._determine_recommended_action(
                damage_type
            )
        )

        replacement_required = (
            self._determine_replacement_requirement(
                damage_type
            )
        )

        return DamageAnalysisResponse(
            damageType=damage_type,
            damageSeverity="UNKNOWN",
            vehiclePart="UNKNOWN",
            recommendedAction=recommended_action,
            partReplacementRequired=replacement_required,
            confidenceScore=primary_damage.confidence,
            analysisMessage=(
                f"{filename} adlı görselde "
                f"{len(damage_detections)} hasarlı bölge "
                f"tespit edildi. En yüksek güven skoru: "
                f"{primary_damage.confidence:.2f}."
            ),
            detections=damage_detections,
        )

    def _build_no_damage_response(
        self,
        filename: str,
        vehicle_detections: list[DetectedObject],
    ) -> DamageAnalysisResponse:
        if vehicle_detections:
            message = (
                f"{filename} adlı görselde araç tespit "
                "edildi ancak güvenilir bir hasarlı alan "
                "bulunamadı."
            )
        else:
            message = (
                f"{filename} adlı görselde güvenilir bir "
                "araç veya hasarlı alan tespit edilemedi."
            )

        return DamageAnalysisResponse(
            damageType="NO_VISIBLE_DAMAGE",
            damageSeverity="NONE",
            vehiclePart="UNKNOWN",
            recommendedAction="NO_ACTION",
            partReplacementRequired=False,
            confidenceScore=0.0,
            analysisMessage=message,
            detections=[],
        )

    @staticmethod
    def _determine_recommended_action(
        damage_type: str,
    ) -> str:
        recommendations = {
            "SCRATCH": "PAINT_TOUCH_UP",
            "DENT": "DENT_REPAIR",
            "CRACK": "PART_REPAIR",
            "PAINT_DAMAGE": "FULL_PAINTING",
            "BROKEN_PART": "PART_REPLACEMENT",
        }

        return recommendations.get(
            damage_type,
            "PART_REPAIR",
        )

    @staticmethod
    def _determine_replacement_requirement(
        damage_type: str,
    ) -> bool:
        return damage_type == "BROKEN_PART"

    @staticmethod
    def _load_image(
        file_content: bytes,
    ) -> Image.Image:
        try:
            image = Image.open(
                BytesIO(file_content)
            )

            image.verify()

            verified_image = Image.open(
                BytesIO(file_content)
            )

            return verified_image.convert("RGB")

        except (
            UnidentifiedImageError,
            OSError,
            ValueError,
        ) as exc:
            raise ValueError(
                "Uploaded file is not a valid image."
            ) from exc

    @staticmethod
    def _extract_detections(
        results: list[Any],
    ) -> list[DetectedObject]:
        detections: list[DetectedObject] = []

        for result in results:
            if result.boxes is None:
                continue

            class_names = result.names

            for box in result.boxes:
                class_id = int(
                    box.cls[0].item()
                )

                confidence = round(
                    float(box.conf[0].item()),
                    4,
                )

                coordinates = (
                    box.xyxy[0]
                    .cpu()
                    .tolist()
                )

                detections.append(
                    DetectedObject(
                        label=str(
                            class_names[class_id]
                        ),
                        confidence=confidence,
                        boundingBox=BoundingBox(
                            x1=round(
                                coordinates[0],
                                2,
                            ),
                            y1=round(
                                coordinates[1],
                                2,
                            ),
                            x2=round(
                                coordinates[2],
                                2,
                            ),
                            y2=round(
                                coordinates[3],
                                2,
                            ),
                        ),
                    )
                )

        return detections