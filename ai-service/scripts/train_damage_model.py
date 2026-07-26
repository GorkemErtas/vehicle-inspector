from pathlib import Path

from ultralytics import YOLO


PROJECT_ROOT = Path(__file__).resolve().parent.parent

DATASET_CONFIG_PATH = (
    PROJECT_ROOT
    / "datasets"
    / "vehicle_damage"
    / "data.yaml"
)

TRAINING_OUTPUT_PATH = (
    PROJECT_ROOT
    / "training-runs"
)


def train_damage_model() -> None:
    if not DATASET_CONFIG_PATH.exists():
        raise FileNotFoundError(
            f"Dataset configuration not found: "
            f"{DATASET_CONFIG_PATH}"
        )

    print(f"Dataset config: {DATASET_CONFIG_PATH.resolve()}")

    model = YOLO("yolo11n.pt")

    model.train(
        data=str(DATASET_CONFIG_PATH),
        epochs=50,
        imgsz=640,
        batch=4,
        patience=10,
        workers=0,
        project=str(TRAINING_OUTPUT_PATH),
        name="vehicle-damage-yolo11n-v2",
        exist_ok=False,
    )


if __name__ == "__main__":
    train_damage_model()