from pathlib import Path

from ultralytics import YOLO


PROJECT_ROOT = Path(__file__).resolve().parent.parent

MODEL_PATH = (
    PROJECT_ROOT
    / "training-runs"
    / "vehicle_part_v1"
    / "weights"
    / "best.pt"
)

TEST_IMAGES_DIR = (
    PROJECT_ROOT
    / "datasets"
    / "vehicle_part"
    / "images"
    / "test"
)

OUTPUT_DIR = (
    PROJECT_ROOT
    / "prediction-runs"
)


def main() -> None:
    if not MODEL_PATH.exists():
        raise FileNotFoundError(
            f"Model bulunamadı: {MODEL_PATH}"
        )

    if not TEST_IMAGES_DIR.exists():
        raise FileNotFoundError(
            f"Test klasörü bulunamadı: {TEST_IMAGES_DIR}"
        )

    model = YOLO(str(MODEL_PATH))

    model.predict(
        source=str(TEST_IMAGES_DIR),
        conf=0.25,
        iou=0.50,
        imgsz=640,
        save=True,
        project=str(OUTPUT_DIR),
        name="vehicle_part_test",
        exist_ok=True,
        verbose=True,
    )


if __name__ == "__main__":
    main()