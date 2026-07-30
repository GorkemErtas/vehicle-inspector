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
DEFAULT_VEHICLE_PART_MODEL_PATH = (
    PROJECT_ROOT / "models" / "vehicle_part_best.pt"
)


class DamageAnalyzer:
    """
    Araç, hasar ve araç parçası tespiti yapan analiz servisi.

    Genel YOLO modeli araç tespiti için,
    özel hasar modeli hasarlı bölgeleri tespit etmek için,
    araç parçası modeli ise hasarın bulunduğu parçayı
    belirlemek için kullanılır.
    """

    VEHICLE_LABELS = {
        "car",
        "truck",
        "bus",
        "motorcycle",
    }

    VEHICLE_PART_MAPPING = {
        "back-bumper": "REAR_BUMPER",
        "back-door": "REAR_DOOR",
        "back-wheel": "REAR_WHEEL",
        "back-window": "REAR_WINDOW",
        "back-windshield": "REAR_WINDSHIELD",
        "fender": "FENDER",
        "front-bumper": "FRONT_BUMPER",
        "front-door": "FRONT_DOOR",
        "front-wheel": "FRONT_WHEEL",
        "front-window": "FRONT_WINDOW",
        "grille": "GRILLE",
        "headlight": "HEADLIGHT",
        "hood": "HOOD",
        "license-plate": "LICENSE_PLATE",
        "mirror": "MIRROR",
        "quarter-panel": "QUARTER_PANEL",
        "rocker-panel": "ROCKER_PANEL",
        "roof": "ROOF",
        "tail-light": "TAIL_LIGHT",
        "trunk": "TRUNK",
        "windshield": "WINDSHIELD",
    }

    def __init__(
        self,
        vehicle_model_path: str | Path = DEFAULT_VEHICLE_MODEL_PATH,
        damage_model_path: str | Path = DEFAULT_DAMAGE_MODEL_PATH,
        vehicle_part_model_path: str | Path = (
            DEFAULT_VEHICLE_PART_MODEL_PATH
        ),
        vehicle_confidence_threshold: float = 0.40,
        damage_confidence_threshold: float = 0.08,
        damage_iou_threshold: float = 0.20,
        vehicle_part_confidence_threshold: float = 0.25,
        minimum_part_overlap_ratio: float = 0.10,
    ) -> None:
        self.vehicle_model_path = Path(vehicle_model_path)
        self.damage_model_path = Path(damage_model_path)
        self.vehicle_part_model_path = Path(
            vehicle_part_model_path
        )

        self.vehicle_confidence_threshold = (
            vehicle_confidence_threshold
        )
        self.damage_confidence_threshold = (
            damage_confidence_threshold
        )
        self.damage_iou_threshold = damage_iou_threshold
        self.vehicle_part_confidence_threshold = (
            vehicle_part_confidence_threshold
        )
        self.minimum_part_overlap_ratio = (
            minimum_part_overlap_ratio
        )

        self._validate_model_paths()

        self.vehicle_model = YOLO(
            str(self.vehicle_model_path)
        )

        self.damage_model = YOLO(
            str(self.damage_model_path)
        )

        self.vehicle_part_model = YOLO(
            str(self.vehicle_part_model_path)
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

        # Önce genel modelle araç tespiti yapılır.
        vehicle_results = self.vehicle_model.predict(
            source=image,
            conf=self.vehicle_confidence_threshold,
            verbose=False,
        )

        vehicle_detections = self._extract_detections(
            vehicle_results
        )

        vehicle_detections = [
            detection
            for detection in vehicle_detections
            if detection.label.lower() in self.VEHICLE_LABELS
        ]

        # En büyük araç kutusunu seçip görüntüyü kırpar.
        analysis_image = self._crop_primary_vehicle(
            image=image,
            vehicle_detections=vehicle_detections,
        )

        # Hasar ve parça modelleri aynı kırpılmış görüntüde çalışır.
        damage_results = self.damage_model.predict(
            source=analysis_image,
            conf=self.damage_confidence_threshold,
            iou=self.damage_iou_threshold,
            max_det=10,
            verbose=False,
        )

        vehicle_part_results = self.vehicle_part_model.predict(
            source=analysis_image,
            conf=self.vehicle_part_confidence_threshold,
            iou=0.50,
            max_det=50,
            verbose=False,
        )

        damage_detections = self._extract_detections(
            damage_results
        )

        vehicle_part_detections = self._extract_detections(
            vehicle_part_results
        )

        print("\n--- DAMAGE DETECTIONS ---")

        for detection in damage_detections:
            print(
                detection.label,
                detection.confidence,
                detection.boundingBox,
            )

        print("\n--- VEHICLE PART DETECTIONS ---")

        for detection in vehicle_part_detections:
            print(
                detection.label,
                detection.confidence,
                detection.boundingBox,
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

        vehicle_part = self._match_damage_to_vehicle_part(
            damage=primary_damage,
            vehicle_parts=vehicle_part_detections,
        )

        return self._build_damage_response(
            filename=safe_filename,
            primary_damage=primary_damage,
            damage_detections=damage_detections,
            vehicle_part=vehicle_part,
        )

    def _validate_model_paths(self) -> None:
        model_paths = {
            "Vehicle detection": self.vehicle_model_path,
            "Damage detection": self.damage_model_path,
            "Vehicle part detection": (
                self.vehicle_part_model_path
            ),
        }

        for model_name, model_path in model_paths.items():
            if not model_path.exists():
                raise FileNotFoundError(
                    f"{model_name} model was not found: "
                    f"{model_path}"
                )

    def _build_damage_response(
        self,
        filename: str,
        primary_damage: DetectedObject,
        damage_detections: list[DetectedObject],
        vehicle_part: str,
    ) -> DamageAnalysisResponse:
        damage_type = self._normalize_enum_value(
            primary_damage.label
        )

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
            vehiclePart=vehicle_part,
            recommendedAction=recommended_action,
            partReplacementRequired=replacement_required,
            confidenceScore=primary_damage.confidence,
            analysisMessage=(
                f"{filename} adlı görselde "
                f"{len(damage_detections)} hasarlı bölge "
                f"tespit edildi. En yüksek güven skoru: "
                f"{primary_damage.confidence:.2f}. "
                f"Tespit edilen araç parçası: "
                f"{vehicle_part}."
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

    def _match_damage_to_vehicle_part(
        self,
        damage: DetectedObject,
        vehicle_parts: list[DetectedObject],
    ) -> str:
        best_vehicle_part = "UNKNOWN"
        best_score = 0.0

        for vehicle_part in vehicle_parts:
            normalized_label = (
                vehicle_part.label
                .strip()
                .lower()
            )

            mapped_vehicle_part = (
                self.VEHICLE_PART_MAPPING.get(
                    normalized_label
                )
            )

            if mapped_vehicle_part is None:
                continue

            overlap_ratio = self._calculate_damage_overlap_ratio(
                damage.boundingBox,
                vehicle_part.boundingBox,
            )

            damage_center_inside = self._is_damage_center_inside(
                damage.boundingBox,
                vehicle_part.boundingBox,
            )

            if (
                overlap_ratio < self.minimum_part_overlap_ratio
                and not damage_center_inside
            ):
                continue

            score = (
                overlap_ratio * 0.80
                + vehicle_part.confidence * 0.20
            )

            if score > best_score:
                best_score = score
                best_vehicle_part = mapped_vehicle_part

        return best_vehicle_part

    @staticmethod
    def _calculate_damage_overlap_ratio(
        damage_box: BoundingBox,
        part_box: BoundingBox,
    ) -> float:
        intersection_x1 = max(
            damage_box.x1,
            part_box.x1,
        )

        intersection_y1 = max(
            damage_box.y1,
            part_box.y1,
        )

        intersection_x2 = min(
            damage_box.x2,
            part_box.x2,
        )

        intersection_y2 = min(
            damage_box.y2,
            part_box.y2,
        )

        intersection_width = max(
            0.0,
            intersection_x2 - intersection_x1,
        )

        intersection_height = max(
            0.0,
            intersection_y2 - intersection_y1,
        )

        intersection_area = (
            intersection_width * intersection_height
        )

        damage_width = max(
            0.0,
            damage_box.x2 - damage_box.x1,
        )

        damage_height = max(
            0.0,
            damage_box.y2 - damage_box.y1,
        )

        damage_area = damage_width * damage_height

        if damage_area <= 0:
            return 0.0

        return intersection_area / damage_area

    @staticmethod
    def _is_damage_center_inside(
        damage_box: BoundingBox,
        part_box: BoundingBox,
    ) -> bool:
        damage_center_x = (
            damage_box.x1 + damage_box.x2
        ) / 2

        damage_center_y = (
            damage_box.y1 + damage_box.y2
        ) / 2

        return (
            part_box.x1
            <= damage_center_x
            <= part_box.x2
            and part_box.y1
            <= damage_center_y
            <= part_box.y2
        )

    @staticmethod
    def _normalize_enum_value(
        value: str,
    ) -> str:
        return (
            value.strip()
            .upper()
            .replace("-", "_")
            .replace(" ", "_")
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

    @staticmethod
    def _crop_primary_vehicle(
        image: Image.Image,
        vehicle_detections: list[DetectedObject],
    ) -> Image.Image:
        if not vehicle_detections:
            return image

        primary_vehicle = max(
            vehicle_detections,
            key=lambda detection: (
                (
                    detection.boundingBox.x2
                    - detection.boundingBox.x1
                )
                * (
                    detection.boundingBox.y2
                    - detection.boundingBox.y1
                )
            ),
        )

        box = primary_vehicle.boundingBox

        image_width, image_height = image.size

        padding_x = int(
            (box.x2 - box.x1) * 0.05
        )

        padding_y = int(
            (box.y2 - box.y1) * 0.05
        )

        left = max(
            0,
            int(box.x1) - padding_x,
        )

        top = max(
            0,
            int(box.y1) - padding_y,
        )

        right = min(
            image_width,
            int(box.x2) + padding_x,
        )

        bottom = min(
            image_height,
            int(box.y2) + padding_y,
        )

        if right <= left or bottom <= top:
            return image

        return image.crop(
            (
                left,
                top,
                right,
                bottom,
            )
        )