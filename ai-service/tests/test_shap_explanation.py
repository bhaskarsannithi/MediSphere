import numpy as np
from sklearn.ensemble import RandomForestClassifier

from config import FEATURES
from prediction.risk_service import generate_shap_explanation


def test_shap_explanation_matches_probability_and_is_sorted():
    rng = np.random.default_rng(7)
    features = rng.normal(size=(80, len(FEATURES)))
    labels = (features[:, 0] + features[:, 1] > 0).astype(int)
    model = RandomForestClassifier(n_estimators=12, random_state=7, min_samples_leaf=2)
    model.fit(features, labels)
    patient = features[:1]

    result = generate_shap_explanation(model, patient, FEATURES)
    explanation = result["explanation"]

    assert len(explanation) == len(FEATURES)
    assert [item["feature"] for item in explanation] == sorted(
        FEATURES, key=lambda feature: next(item["importance"] for item in explanation if item["feature"] == feature), reverse=True
    )
    assert all(np.isfinite(item["shapValue"]) for item in explanation)
    assert all(item["direction"] == ("INCREASES_RISK" if item["shapValue"] >= 0 else "DECREASES_RISK") for item in explanation)
    assert result["outputSpace"] == "probability"
    assert abs(result["explainedValue"] - model.predict_proba(patient)[0, 1]) < 1e-5
    assert result["consistencyError"] < 1e-5
