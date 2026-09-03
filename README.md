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

MongoDB collections are `patients`, `health_twins`, `vitals`, `lab_results`, `fhir_resources`, `consents`, `audit_logs`, and `users` (the user model is reserved for a future persistent identity provider).

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
