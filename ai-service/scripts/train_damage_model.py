from pathlib import Path

import torch
from ultralytics import YOLO


PROJECT_ROOT = Path(__file__).resolve().parent.parent

DATASET_CONFIG_PATH = (
    PROJECT_ROOT
    / "datasets"
    / "vehicle_damage_type"
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

    if torch.cuda.is_available():
        device: int | str = 0
        print(
            "GPU kullanılıyor:",
            torch.cuda.get_device_name(0),
        )
    else:
        device = "cpu"
        print("CUDA bulunamadı. CPU kullanılacak.")

    print(
        "Dataset config:",
        DATASET_CONFIG_PATH.resolve(),
    )

    print(
        "Eğitim çıktısı:",
        TRAINING_OUTPUT_PATH.resolve(),
    )

    model = YOLO("yolo11n.pt")

    model.train(
        data=str(DATASET_CONFIG_PATH),
        epochs=80,
        imgsz=640,
        batch=8,
        device=device,
        workers=2 if device == 0 else 0,
        patience=15,
        project=str(TRAINING_OUTPUT_PATH),
        name="vehicle_damage_type_v1",
        pretrained=True,
        cache=False,
        plots=True,
        exist_ok=False,
    )


if __name__ == "__main__":
    train_damage_model()