# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:


## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.

# MediSphere Cognitive Twin

MediSphere is a Milestone 1 clinical data foundation using synthetic healthcare data. It provides a React Patient 360 dashboard, Spring Boot REST APIs, MongoDB persistence, FHIR R4-shaped resource ingestion, and Kafka vital streaming. It is a demo system with HIPAA-oriented security controls, not a HIPAA compliance certification.

## Stack and architecture

React/Vite -> Spring Boot 3.5 / Java 25 -> MongoDB (`medisphere`) and Kafka (`vital-signs`). The main flow is synthetic wearable -> Kafka producer -> consumer -> `vitals` collection -> one current `health_twins` document -> Patient 360.

MongoDB collections are `patients`, `health_twins`, `vitals`, `alerts`, `lab_results`, `fhir_resources`, `consents`, `audit_logs`, and `users` (the user model is reserved for a future persistent identity provider).

## Milestone 3: Continuous monitoring and alerts

Implemented monitoring extends the existing `vital-signs` Kafka flow:

`wearable simulator -> Kafka -> VitalStreamService -> validation -> VitalService/Digital Twin -> AlertService -> MongoDB -> React Monitoring`

- **Validation:** required patient/type/value/unit fields, known vital types, physiological bounds, patient existence, and future timestamp checks are applied before a vital is persisted or evaluated. Malformed Kafka events are rejected without creating clinical alerts.
- **Anomaly detection:** the alert service calculates a rolling baseline from up to 20 prior patient readings of the same type. With enough history it calculates a deterministic deviation score using standard deviation; configured clinical thresholds remain the final alert decision layer.
- **Clinical rules:** heart rate `>=140`/`<=35` is `CRITICAL`, `>=120`/`<=45` is `HIGH`; SpO2 `<=85` is `CRITICAL` and `<=90` is `HIGH`; temperature, respiratory-rate, and systolic blood-pressure boundaries produce `HIGH` alerts. Other statistical anomalies receive `MEDIUM` severity.
- **Persistence and lifecycle:** alerts are stored in the `alerts` collection with observed/baseline values, anomaly score, detection method, routing role, source, correlation ID, timestamps, and `NEW -> ACKNOWLEDGED -> RESOLVED/DISMISSED` status fields.
- **Routing and notification:** critical/high alerts route to `ROLE_DOCTOR`; lower-severity alerts route to `ROLE_NURSE`. `notificationStatus=IN_APP` records the working in-app notification channel shown by the monitoring page. Email/mobile push adapters remain planned. ADMIN, DOCTOR, and NURSE can acknowledge; only ADMIN and DOCTOR can resolve or dismiss. Actions are written to the existing audit log.
- **Alert fatigue:** active alerts with the same patient, vital type, and rule are grouped for the configurable `MEDISPHERE_ALERT_COOLDOWN_MINUTES` window (default 15 minutes), incrementing `suppressedCount` instead of creating another alert.
- **Monitoring UI:** `/monitoring` polls active alerts and statistics every five seconds, displays measured persisted metrics, and supports acknowledgement. Patient 360 includes the patient alert list. This is polling-based live display; no push/mobile delivery adapter is implemented yet.
- **Latency:** event received, detected, created, and acknowledgement timestamps are stored. Processing and acknowledgement latency are calculated from actual timestamps; averages show `N/A` until data exists.

### Milestone 3 demonstration

1. Start Compose and the backend as described below, then run the Vite frontend.
2. Log in as `admin` / `medisphere-demo` and open **Monitoring**.
3. Publish an event through the existing wearable endpoint. This remains the `vital-signs` topic and is consumed by the existing Kafka listener:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/wearables/simulate -Authentication Basic -Credential (Get-Credential) -ContentType 'application/json' -Body '{"patientId":"MS-10001","vitalType":"HEART_RATE","value":145,"unit":"bpm"}'
```

4. Wait for the consumer and refresh/polling cycle. The alert is persisted, assigned to `ROLE_DOCTOR`, and appears as `CRITICAL` with its observed value and detection method.
5. Acknowledge it in the page. The acknowledgement actor, timestamp, and latency are persisted and audited.
6. After acknowledgement, use **Resolve** or **Dismiss**. The lifecycle actor and timestamp are persisted; nurses can acknowledge but cannot resolve or dismiss.

The backend includes a reproducible synthetic labeled heart-rate evaluation in `AnomalyEvaluationTest`: 8 readings, 2 labeled anomalies, 2 true positives, and 0 false positives produce an actual precision of `1.0` (100%) for the tested rule set. This is a small synthetic unit evaluation, not a clinical validation study. A live Kafka test harness is future work; the current live demonstration was executed through the existing wearable simulator and Kafka consumer flow.

## Milestone 4: Care plan and intervention

Milestone 4 extends the existing Patient -> Digital Twin -> AI prediction -> alert pipeline with clinician-reviewed care plans. It is an academic decision-support workflow using synthetic data and configurable demo rules, not medical advice or clinical validation.

```text
Digital Twin + latest prediction/vitals/alerts
			  -> deterministic care-plan generator
			  -> guideline and safety validation
			  -> provider review (approve/modify/reject)
			  -> active intervention
			  -> adherence records + outcome measurements
			  -> Digital Twin update
```

The backend stores goals, interventions, monitoring tasks, expected outcomes, risk context, validation results, provider review, and audit timeline in the indexed `careplans` collection. Adherence and measured outcomes use separate indexed `adherence_records` and `outcomes` collections. The generator reuses `PatientService`, `DigitalTwinService`, `RiskPredictionRepository`, `VitalRepository`, and `AlertRepository`; it does not create another prediction model or patient data store.

Care-plan APIs are available under `/api/careplans`: `POST /generate`, `GET /{id}`, `GET /patient/{patientId}`, `PUT /{id}`, `POST /{id}/validate`, `POST /{id}/approve`, `POST /{id}/modify`, `POST /{id}/reject`, `POST /{id}/activate`, `POST|GET /{id}/adherence`, `GET /adherence/patient/{patientId}`, `GET /{id}/adherence/statistics`, and `POST|GET /{id}/outcomes`. Provider approval actions require the existing `ADMIN` or `DOCTOR` roles. Medication-related recommendations fail the demo guideline safety check until provider review; no medication is prescribed or changed automatically.

The React `/careplans` page reuses the existing layout, navigation, API client, tables, badges, and stat cards. `/careplans/{carePlanId}` provides the dedicated details workflow with risk reasoning, safety checks, timeline, provider comments, intervention modification, adherence status, and outcome measurement. A demonstration is: sign in as `admin` / `medisphere-demo`, select a synthetic patient, generate a plan, validate it, approve it, record adherence, and add a measured outcome. Existing Kafka remains intact for `vital-signs`; Milestone 4 adds the configurable `careplan-events` topic for `careplan.generated`, `careplan.validated`, `careplan.modified`, `careplan.approve`, `careplan.reject`, `intervention.completed`, `intervention.missed`, and `outcome.updated` events.

## Prerequisites

Windows 10/11, Java 25, Maven 3.9.x, Node.js LTS, and Docker Desktop with WSL 2 integration. Maven must be available as `mvn.cmd` in PATH.

## Start locally

From the repository root in Windows CMD:

```cmd
docker compose -f backend\docker-compose.yml up -d
cd backend
mvn.cmd spring-boot:run
```

In a second CMD window:

```cmd
cd c:\Users\chakk\Desktop\MediSphere
npm install
npm run dev
```

Frontend: http://localhost:5173  
Backend health: http://localhost:8080/actuator/health  
Swagger UI: http://localhost:8080/swagger-ui.html

Demo Basic Auth credentials default to `admin` / `medisphere-demo`. Override with `MEDISPHERE_DEMO_USERNAME` and `MEDISPHERE_DEMO_PASSWORD`; never use these defaults outside local development.

## APIs

- Patients: `GET/POST /api/patients`, `GET /api/patients/{patientId}`
- Twins: `GET /api/twins`, `GET /api/twins/{patientId}`
- Vitals/labs: `GET /api/vitals/{patientId}`, `POST /api/vitals`, `GET /api/labs/{patientId}`, `POST /api/labs`
- FHIR: `POST /api/fhir/Patient`, `POST /api/fhir/Observation`, `POST /api/fhir/resources`, `GET /api/fhir`, `GET /api/fhir/resources/{id}`, `POST /api/fhir/validate`
- Wearables: `POST /api/wearables/simulate`; events are published to `vital-signs` and consumed into MongoDB
- Monitoring: `GET /api/alerts`, `GET /api/alerts/events`, `GET /api/alerts/patient/{patientId}`, `GET /api/alerts/statistics`, `POST /api/alerts/{alertId}/acknowledge`, and `POST /api/alerts/{alertId}/RESOLVED|DISMISSED`
- Care plans: `POST /api/careplans/generate`, `GET /api/careplans/{id}`, `GET /api/careplans/patient/{patientId}`, `PUT /api/careplans/{id}`, validation/provider review endpoints, adherence endpoints, and outcome endpoints
- Consent: `GET/POST /api/consents`, `GET /api/consents/{patientId}`, `POST /api/consents/{consentId}/revoke`
- Audit: `GET /api/audit` (ADMIN role)

FHIR support is limited to Patient, Observation, and DiagnosticReport payloads with basic structural validation. Synthetic seed data creates five demo patients when the database is empty.

## Roles and consent

Basic Auth exposes `ADMIN`, `DOCTOR`, and `NURSE` demo identities. Patient/twin/FHIR reads require an authenticated role. Vital and wearable writes require a clinical role. Consent writes require ADMIN or DOCTOR. Audit reads require ADMIN. Consent records and important writes create audit records.

## End-to-end demonstration

1. Start Compose, the backend, and the Vite frontend.
2. Log in as `admin` / `medisphere-demo`.
3. Open Patients and select `MS-10001`; Patient 360 reads patient, twin, lab, and FHIR APIs.
4. Send a synthetic heart rate event:

```cmd
curl -u admin:medisphere-demo -H "Content-Type: application/json" -d "{\"patientId\":\"MS-10001\",\"vitalType\":\"HEART_RATE\",\"value\":82,\"unit\":\"bpm\"}" http://localhost:8080/api/wearables/simulate
```

5. Refresh Patient 360 after the Kafka consumer processes the event. The latest twin value and `lastUpdated` reflect the message; `/api/audit` records the vital ingestion.

## Testing and limitations

Run backend tests with `cd backend` then `mvn.cmd clean test`. Run frontend checks with `npm run lint` and `npm run build`. The demo uses HTTP Basic with in-memory users and basic FHIR validation; production SMART-on-FHIR/OIDC, persistent identity management, real EHR/wearable connectors, and advanced clinical AI are intentionally outside Milestone 1.

## Milestone 2: AI risk decision support

Milestone 2 adds a separate Python FastAPI service in `ai-service/`. Spring Boot remains the authenticated public API: the browser never calls the AI service or MongoDB directly. On a protected prediction request, Spring Boot derives a minimized feature vector from the existing patient, vital, and lab records, calls the AI service, stores the exact model-versioned result in `risk_predictions`, audits the action, and writes a compact risk summary to the patient's `health_twins` record.

The AI training dataset is generated locally from a fixed random seed. It is synthetic and de-identified; no real patient data or PHI is used for model training. The service uses persisted `RandomForestClassifier` models for CVD and diabetes-complication risk. It calculates its own accuracy, precision, recall, F1, ROC-AUC, confusion matrix, Brier score, calibration curve, and sex/age-group audit from the holdout data. Metrics are never hard-coded. SHAP `TreeExplainer` calculates patient-specific probability-space contributions from the exact persisted random-forest model. Each saved explanation includes the input value, signed contribution, direction, importance, base value, and a consistency error against the predicted probability.

Federated learning is supplied as an on-demand FedAvg demonstration. It creates three separate local partitions (`Hospital A`, `Hospital B`, and `Hospital C`) and averages local model parameters; raw rows are not aggregated. The status endpoint reports actual rounds, loss, accuracy, and the convergence result. It remains `NOT_STARTED` until training is explicitly requested, rather than claiming a completed run.

### Run the AI service

The service image uses Python 3.11 and pinned prediction dependencies including SHAP 0.44.1, scikit-learn 1.3.2, NumPy 1.25.2, pandas 2.1.4, and SciPy 1.11.4.

```powershell
docker compose -f backend/docker-compose.yml up -d --build
```

The AI service is then available at `http://localhost:8000/health`. The first prediction trains and persists the two local synthetic models. To run the federated demonstration explicitly:

```powershell
Invoke-RestMethod -Method Post "http://localhost:8000/federated/train?rounds=8"
```

### AI API

All Spring Boot AI endpoints require the existing Basic Auth roles:

- `POST /api/ai/predict/cvd/{patientId}`
- `POST /api/ai/predict/diabetes/{patientId}`
- `GET /api/ai/predictions/{patientId}`
- `GET /api/ai/explanations/{predictionId}`
- `GET /api/ai/models`
- `GET /api/ai/federated/status`

The React **AI Risk Prediction** page exposes the patient selector, calculated results, SHAP bars, live model metrics, and federated status. It displays the required clinical safety statement: this prototype estimates risk only and does not diagnose, prescribe, or replace an authorized clinician's judgment.

### SHAP interpretation

The CVD and diabetes predictions use `shap.TreeExplainer` because the persisted models are random forests. Contributions explain class-1 risk in probability space: the base value plus all SHAP contributions approximately equals the model's predicted probability. Positive values contributed toward higher model risk and negative values toward lower model risk; they do not establish causation. Explanations are returned by the existing prediction endpoints and persisted in each `risk_predictions` document. The UI presents the top contributions on AI Risk Prediction and Patient 360 with a clinical-review disclaimer.
