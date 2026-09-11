from fastapi import FastAPI, HTTPException, Query
from pydantic import BaseModel, Field

from config import FEATURES
from prediction.risk_service import model_status, predict

app = FastAPI(title="MediSphere AI Service", version="1.0.0")


class PredictionRequest(BaseModel):
    patientId: str = Field(min_length=1)
    features: dict[str, float]


@app.get("/health")
def health():
    return {"status": "UP", "service": "medisphere-ai", "requiredFeatures": FEATURES}


@app.get("/models")
def models():
    return model_status()


@app.post("/predict/{model_type}")
def create_prediction(model_type: str, request: PredictionRequest):
    normalized = "CVD" if model_type.lower() == "cvd" else "DIABETES_COMPLICATION" if model_type.lower() in {"diabetes", "diabetes_complication"} else None
    if not normalized:
        raise HTTPException(status_code=404, detail="Supported model types are cvd and diabetes")
    try:
        return predict(normalized, request.patientId, request.features)
    except ValueError as error:
        raise HTTPException(status_code=422, detail=str(error)) from error


@app.get("/federated/status")
def federated_status():
    try:
        from training.federated_training import load_status

        return load_status()
    except ImportError:
        return {"status": "UNAVAILABLE", "currentRound": 0, "clients": 0, "message": "Federated training dependencies are not installed in the prediction image."}


@app.post("/federated/train")
def federated_train(rounds: int = Query(default=8, ge=2, le=50)):
    try:
        from training.federated_training import run_federated_training

        return run_federated_training(rounds)
    except ImportError as error:
        raise HTTPException(status_code=503, detail="Federated training dependencies are not installed in the prediction image.") from error
    except Exception as error:
        raise HTTPException(status_code=500, detail="Federated training failed; inspect AI service logs.") from error
