# MediSphere AI Service

This FastAPI service trains only on reproducible synthetic, de-identified data. It is a clinical decision-support prototype and does not diagnose or prescribe.

Use Docker to run it with the compatible Python 3.11 environment:

```powershell
docker compose -f backend/docker-compose.yml up --build ai-service
```

The first model request trains the local CVD and diabetes models and writes validation reports and a model registry under `models/`. `POST /federated/train?rounds=8` runs FedAvg across the three separate hospital CSV partitions without sending raw records to the aggregation step. CVD and diabetes predictions use SHAP `TreeExplainer` on the exact persisted random-forest model; explanations are returned in probability space and include a consistency check against the predicted probability.
