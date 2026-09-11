from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
DATA_DIR = BASE_DIR / "data"
MODEL_DIR = BASE_DIR / "models"
REPORT_DIR = BASE_DIR / "reports"
RANDOM_SEED = 20260910
MODEL_SCHEMA_VERSION = "synthetic-v1"

FEATURES = [
    "age", "sex", "bmi", "systolic_bp", "diastolic_bp", "heart_rate",
    "hba1c", "glucose", "cholesterol", "smoking", "diabetes",
    "family_history", "previous_cvd", "diabetes_duration", "kidney_indicator",
]
