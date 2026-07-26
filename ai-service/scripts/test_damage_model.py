from pathlib import Path

from ultralytics import YOLO


PROJECT_ROOT = Path(__file__).resolve().parent.parent

MODEL_PATH = PROJECT_ROOT / "models" / "best.pt"
TEST_IMAGE_PATH = PROJECT_ROOT / "test-images" / "carsc.jpg"
OUTPUT_PATH = PROJECT_ROOT / "prediction-runs"


def test_damage_model() -> None:
    if not MODEL_PATH.exists():
        raise FileNotFoundError(
            f"Model was not found: {MODEL_PATH}"
        )

    if not TEST_IMAGE_PATH.exists():
        raise FileNotFoundError(
            f"Test image was not found: {TEST_IMAGE_PATH}"
        )

    print(f"Model path: {MODEL_PATH.resolve()}")

    model = YOLO(str(MODEL_PATH))

    results = model.predict(
        source=str(TEST_IMAGE_PATH),
        conf=0.05,
        iou=0.25,
        max_det=20,
        save=True,
        project=str(OUTPUT_PATH),
        name="damage-test",
        exist_ok=True,
    )

    for result in results:
        box_count = 0 if result.boxes is None else len(result.boxes)
        print(f"Detected boxes: {box_count}")

        if result.boxes is None or len(result.boxes) == 0:
            print("No reliable damage detection was produced.")
            continue

        for box in result.boxes:
            class_id = int(box.cls[0].item())
            confidence = float(box.conf[0].item())

            print(
                f"Class: {result.names[class_id]}, "
                f"confidence: {confidence:.4f}"
            )


if __name__ == "__main__":
    test_damage_model()