import json
from datetime import datetime, timezone

import joblib
import numpy as np
import shap

from config import FEATURES, MODEL_DIR
from training.train_models import ensure_models
from versioning.model_registry import load_records

FEATURE_LABELS = {
    "age": "Age",
    "sex": "Sex",
    "bmi": "BMI",
    "systolic_bp": "Systolic blood pressure",
    "diastolic_bp": "Diastolic blood pressure",
    "heart_rate": "Heart rate",
    "hba1c": "HbA1c",
    "glucose": "Glucose",
    "cholesterol": "Cholesterol",
    "smoking": "Smoking",
    "diabetes": "Diabetes",
    "family_history": "Family history",
    "previous_cvd": "Previous CVD",
    "diabetes_duration": "Diabetes duration",
    "kidney_indicator": "Kidney indicator",
}


def _model_type_name(model_type):
    return "cvd" if model_type == "CVD" else "diabetes_complication"


def _record(model_type):
    return next(record for record in load_records() if record["modelType"] == model_type and record["status"] == "ACTIVE")


def _category(probability):
    if probability >= .30: return "HIGH"
    if probability >= .15: return "MODERATE"
    return "LOW"


def generate_shap_explanation(model, values, feature_names):
    """Explain class-1 probability using the exact persisted tree model input."""
    explainer = shap.TreeExplainer(model)
    raw_values = explainer.shap_values(values)
    contributions = np.asarray(raw_values[1] if isinstance(raw_values, list) else raw_values)
    contributions = contributions.reshape(values.shape[0], -1)[0]
    expected = np.asarray(explainer.expected_value).reshape(-1)
    base_value = float(expected[1] if expected.size > 1 else expected[0])
    predicted_probability = float(model.predict_proba(values)[0, 1])
    explained_probability = base_value + float(contributions.sum())
    consistency_error = abs(explained_probability - predicted_probability)
    if not np.isfinite(contributions).all() or not np.isfinite([base_value, explained_probability, consistency_error]).all():
        raise ValueError("SHAP returned a non-finite explanation")
    if consistency_error > 1e-5:
        raise ValueError("SHAP explanation is inconsistent with the model probability")

    explanation = []
    for index, feature in enumerate(feature_names):
        contribution = float(contributions[index])
        explanation.append({
            "feature": feature,
            "label": FEATURE_LABELS.get(feature, feature.replace("_", " ").title()),
            "value": float(values[0, index]),
            "shapValue": contribution,
            "contribution": contribution,
            "direction": "INCREASES_RISK" if contribution >= 0 else "DECREASES_RISK",
            "importance": abs(contribution),
            "summary": f"{FEATURE_LABELS.get(feature, feature)} contributed to "
                       f"{'higher' if contribution >= 0 else 'lower'} predicted CVD risk.",
        })
    return {
        "explanation": sorted(explanation, key=lambda item: item["importance"], reverse=True),
        "outputSpace": "probability",
        "baseValue": base_value,
        "explainedValue": explained_probability,
        "consistencyError": consistency_error,
    }


def predict(model_type, patient_id, features):
    ensure_models()
    missing = [feature for feature in FEATURES if feature not in features or features[feature] is None]
    if missing:
        raise ValueError("Missing required model features: " + ", ".join(missing))
    model = joblib.load(MODEL_DIR / _model_type_name(model_type) / "model.joblib")
    values = np.asarray([[float(features[feature]) for feature in FEATURES]])
    probability = float(model.predict_proba(values)[0, 1])
    shap_result = generate_shap_explanation(model, values, FEATURES)
    record = _record(model_type)
    return {
        "patientId": patient_id, "modelType": model_type, "riskScore": probability,
        "riskPercentage": round(probability * 100, 2), "riskCategory": _category(probability),
        "confidence": max(probability, 1 - probability), "modelVersion": record["modelId"],
        "predictionTimestamp": datetime.now(timezone.utc).isoformat(), "explanation": shap_result["explanation"],
        "shapOutputSpace": shap_result["outputSpace"], "shapBaseValue": shap_result["baseValue"],
        "shapExplainedValue": shap_result["explainedValue"],
        "shapConsistencyError": shap_result["consistencyError"],
        "modelMetrics": record["metrics"],
    }


def model_status():
    ensure_models()
    return load_records()
