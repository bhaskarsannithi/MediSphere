import json
from datetime import datetime, timezone

from config import MODEL_DIR


def registry_path():
    return MODEL_DIR / "registry.json"


def save_record(record):
    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    records = load_records()
    record["trainingTimestamp"] = datetime.now(timezone.utc).isoformat()
    records = [item for item in records if item["modelId"] != record["modelId"]] + [record]
    registry_path().write_text(json.dumps(records, indent=2), encoding="utf-8")
    return record


def load_records():
    path = registry_path()
    return json.loads(path.read_text(encoding="utf-8")) if path.exists() else []
