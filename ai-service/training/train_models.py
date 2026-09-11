import json
from pathlib import Path

import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split

from config import FEATURES, MODEL_DIR, MODEL_SCHEMA_VERSION, RANDOM_SEED
from data.generate_dataset import create_partitions
from validation.metrics import bias_audit, calibration, evaluate
from versioning.model_registry import save_record


def train_model(model_type, target):
    data = create_partitions()
    train, test = train_test_split(data, test_size=.2, stratify=data[target], random_state=RANDOM_SEED)
    model = RandomForestClassifier(n_estimators=250, min_samples_leaf=5, class_weight="balanced", random_state=RANDOM_SEED)
    model.fit(train[FEATURES], train[target])
    probability = model.predict_proba(test[FEATURES])[:, 1]
    metrics = evaluate(test[target], probability)
    directory = MODEL_DIR / model_type.lower()
    directory.mkdir(parents=True, exist_ok=True)
    joblib.dump(model, directory / "model.joblib")
    report = {"metrics": metrics, "calibration": calibration(test[target], probability), "biasAudit": bias_audit(test, test[target].to_numpy(), probability)}
    (directory / "validation.json").write_text(json.dumps(report, indent=2), encoding="utf-8")
    return save_record({
        "modelId": f"{model_type}-RF-v1.0", "modelType": model_type, "version": "v1.0",
        "algorithm": "RandomForestClassifier", "trainingMethod": "local synthetic baseline",
        "datasetVersion": MODEL_SCHEMA_VERSION, "status": "ACTIVE", "metrics": metrics,
    })


def ensure_models():
    cvd = MODEL_DIR / "cvd" / "model.joblib"
    diabetes = MODEL_DIR / "diabetes_complication" / "model.joblib"
    if not cvd.exists(): train_model("CVD", "cvd_target")
    if not diabetes.exists(): train_model("DIABETES_COMPLICATION", "diabetes_target")


if __name__ == "__main__":
    ensure_models()
