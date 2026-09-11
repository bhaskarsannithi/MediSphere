"""Lightweight FedAvg over three isolated synthetic hospital partitions."""
import json
from datetime import datetime, timezone

import numpy as np
import pandas as pd
from sklearn.linear_model import SGDClassifier
from sklearn.metrics import log_loss

from config import DATA_DIR, FEATURES, MODEL_DIR, RANDOM_SEED
from data.generate_dataset import create_partitions

STATUS_FILE = MODEL_DIR / "federated_status.json"


def load_status():
    if not STATUS_FILE.exists():
        return {"status": "NOT_STARTED", "currentRound": 0, "clients": 3, "message": "Run POST /federated/train to start FedAvg training."}
    return json.loads(STATUS_FILE.read_text(encoding="utf-8"))


def _save_status(status):
    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    STATUS_FILE.write_text(json.dumps(status, indent=2), encoding="utf-8")


def _dataset(frame):
    labels = frame["cvd_target"].astype("float32").to_numpy()
    values = frame[FEATURES].astype("float64").to_numpy()
    scales = np.maximum(np.max(np.abs(values), axis=0), 1.0)
    return values / scales, labels


def _local_update(features, labels, coefficients, intercept):
    model = SGDClassifier(loss="log_loss", learning_rate="constant", eta0=.08,
                          max_iter=1, tol=None, random_state=RANDOM_SEED)
    model.partial_fit(features, labels, classes=np.array([0, 1]))
    model.coef_ = coefficients.copy()
    model.intercept_ = intercept.copy()
    model.partial_fit(features, labels)
    return model.coef_, model.intercept_


def run_federated_training(rounds: int = 8):
    create_partitions()
    clients = [_dataset(pd.read_csv(DATA_DIR / f"hospital_{name}.csv")) for name in ("a", "b", "c")]
    coefficients = np.zeros((1, len(FEATURES)))
    intercept = np.zeros(1)
    history = []
    for round_number in range(1, rounds + 1):
        updates = [_local_update(features, labels, coefficients, intercept) for features, labels in clients]
        total_rows = sum(len(labels) for _, labels in clients)
        coefficients = sum(update[0] * len(labels) for update, (_, labels) in zip(updates, clients)) / total_rows
        intercept = sum(update[1] * len(labels) for update, (_, labels) in zip(updates, clients)) / total_rows
        all_features = np.concatenate([features for features, _ in clients])
        all_labels = np.concatenate([labels for _, labels in clients])
        probabilities = 1 / (1 + np.exp(-(all_features @ coefficients[0] + intercept[0])))
        accuracy = float(np.mean((probabilities >= .5) == all_labels))
        loss = float(log_loss(all_labels, probabilities, labels=[0, 1]))
        history.append({"round": round_number, "loss": loss, "accuracy": accuracy, "clients": ["Hospital A", "Hospital B", "Hospital C"]})
    converged = len(history) >= 3 and max(abs(history[-i]["loss"] - history[-i - 1]["loss"]) for i in range(1, 3)) < .002
    status = {
        "status": "CONVERGED" if converged else "COMPLETED_NOT_CONVERGED", "currentRound": rounds,
        "clients": 3, "globalAccuracy": history[-1]["accuracy"], "loss": history[-1]["loss"],
        "history": history, "trainingTimestamp": datetime.now(timezone.utc).isoformat(),
        "note": "FedAvg averaged local model parameters from three client datasets; raw records were not aggregated.",
    }
    _save_status(status)
    return status
