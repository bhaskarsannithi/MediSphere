import numpy as np
from sklearn.calibration import calibration_curve
from sklearn.metrics import accuracy_score, brier_score_loss, confusion_matrix, f1_score, precision_score, recall_score, roc_auc_score


def evaluate(y_true, probability, threshold: float = .5):
    prediction = (np.asarray(probability) >= threshold).astype(int)
    return {
        "accuracy": float(accuracy_score(y_true, prediction)),
        "precision": float(precision_score(y_true, prediction, zero_division=0)),
        "recall": float(recall_score(y_true, prediction, zero_division=0)),
        "f1": float(f1_score(y_true, prediction, zero_division=0)),
        "rocAuc": float(roc_auc_score(y_true, probability)),
        "brierScore": float(brier_score_loss(y_true, probability)),
        "confusionMatrix": confusion_matrix(y_true, prediction).tolist(),
    }


def calibration(y_true, probability):
    observed, predicted = calibration_curve(y_true, probability, n_bins=8, strategy="quantile")
    return {"predicted": predicted.tolist(), "observed": observed.tolist()}


def bias_audit(data, y_true, probability):
    audits = {}
    groups = {
        "sex": {"female": data.sex == 0, "male": data.sex == 1},
        "age": {"18-40": data.age <= 40, "41-60": (data.age > 40) & (data.age <= 60), "61+": data.age > 60},
    }
    for dimension, values in groups.items():
        audits[dimension] = {}
        for label, mask in values.items():
            if mask.sum() and len(set(np.asarray(y_true)[mask])) > 1:
                audits[dimension][label] = evaluate(np.asarray(y_true)[mask], np.asarray(probability)[mask])
    return audits
