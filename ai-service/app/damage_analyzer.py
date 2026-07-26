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


class DamageAnalyzer:
    """
    Araç hasar analiz servisidir.

    Şimdilik genel amaçlı YOLO modeli kullanılır.
    Daha sonra özel araç hasar modeli models/best.pt
    dosyasından yüklenecektir.
    """

    VEHICLE_LABELS = {
        "car",
        "truck",
        "bus",
        "motorcycle",
    }

    def __init__(
        self,
        model_path: str = "yolo11n.pt",
        confidence_threshold: float = 0.40,
    ) -> None:
        self.model_path = model_path
        self.confidence_threshold = confidence_threshold
        self.model = YOLO(model_path)

    def analyze(
        self,
        file_content: bytes,
        filename: str,
    ) -> DamageAnalysisResponse:
        safe_filename = Path(
            filename or "vehicle.jpg"
        ).name.lower()

        image = self._load_image(file_content)

        results = self.model.predict(
            source=image,
            conf=self.confidence_threshold,
            verbose=False,
        )

        detections = self._extract_detections(results)

        vehicle_detections = [
            detection
            for detection in detections
            if detection.label in self.VEHICLE_LABELS
        ]

        if not vehicle_detections:
            return DamageAnalysisResponse(
                damageType="UNKNOWN",
                damageSeverity="NONE",
                vehiclePart="UNKNOWN",
                recommendedAction="MANUAL_INSPECTION",
                partReplacementRequired=False,
                confidenceScore=0.0,
                analysisMessage=(
                    f"{safe_filename} adlı görselde "
                    "araç tespit edilemedi."
                ),
                detections=detections,
            )

        primary_vehicle = max(
            vehicle_detections,
            key=self._calculate_detection_area,
        )

        return DamageAnalysisResponse(
            damageType="PENDING_CUSTOM_MODEL",
            damageSeverity="UNKNOWN",
            vehiclePart="UNKNOWN",
            recommendedAction="MANUAL_INSPECTION",
            partReplacementRequired=False,
            confidenceScore=primary_vehicle.confidence,
            analysisMessage=(
                "Araç tespit edildi. Hasar türü ve hasar "
                "seviyesi, özel hasar modeli entegre "
                "edildikten sonra belirlenecektir."
            ),
            detections=detections,
        )

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

    def _extract_detections(
        self,
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
                        label=class_names[class_id],
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

    @staticmethod
    def _calculate_detection_area(
        detection: DetectedObject,
    ) -> float:
        bounding_box = detection.boundingBox

        width = max(
            0.0,
            bounding_box.x2 - bounding_box.x1,
        )

        height = max(
            0.0,
            bounding_box.y2 - bounding_box.y1,
        )

        return width * height