from pathlib import Path

import torch
from ultralytics import YOLO


PROJECT_ROOT = Path(__file__).resolve().parent.parent

DATASET_YAML = (
    PROJECT_ROOT
    / "datasets"
    / "vehicle_part"
    / "data.yaml"
)

TRAINING_RUNS_DIR = (
    PROJECT_ROOT
    / "training-runs"
)


def main() -> None:
    if not DATASET_YAML.exists():
        raise FileNotFoundError(
            f"Dataset YAML bulunamadı: {DATASET_YAML}"
        )

    device: int | str

    if torch.cuda.is_available():
        device = 0
        print(
            "GPU kullanılıyor:",
            torch.cuda.get_device_name(0),
        )
    else:
        device = "cpu"
        print("CUDA bulunamadı. CPU kullanılacak.")

    print("Dataset YAML:", DATASET_YAML)
    print("Eğitim çıktısı:", TRAINING_RUNS_DIR)

    model = YOLO("yolo11n.pt")

    model.train(
        data=str(DATASET_YAML),
        epochs=40,
        imgsz=640,
        batch=8,
        device=0,
        workers=2,
        patience=10,
        project=str(TRAINING_RUNS_DIR),
        name="vehicle_part_v1",
        pretrained=True,
        cache=False,
        plots=True,
        exist_ok=False,
    )


if __name__ == "__main__":
    main()