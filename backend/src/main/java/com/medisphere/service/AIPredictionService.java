package com.medisphere.service;

import com.medisphere.model.AIModel;
import com.medisphere.model.HealthTwin;
import com.medisphere.model.Patient;
import com.medisphere.model.RiskPrediction;
import com.medisphere.repository.AIModelRepository;
import com.medisphere.repository.RiskPredictionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AIPredictionService {
    private final PatientService patientService;
    private final AIFeatureService featureService;
    private final AIClient aiClient;
    private final RiskPredictionRepository predictionRepository;
    private final AIModelRepository modelRepository;
    private final DigitalTwinService twinService;
    private final AuditService auditService;

    public AIPredictionService(PatientService patientService, AIFeatureService featureService, AIClient aiClient,
                               RiskPredictionRepository predictionRepository, AIModelRepository modelRepository,
                               DigitalTwinService twinService, AuditService auditService) {
        this.patientService = patientService; this.featureService = featureService; this.aiClient = aiClient;
        this.predictionRepository = predictionRepository; this.modelRepository = modelRepository;
        this.twinService = twinService; this.auditService = auditService;
    }

    public RiskPrediction predict(String modelType, String patientId, String actor) {
        Patient patient = patientService.getPatientById(patientId);
        AIFeatureService.FeaturePayload payload = featureService.build(patient);
        Map<String, Object> response = aiClient.predict(modelType, patientId, payload.features());
        RiskPrediction prediction = new RiskPrediction();
        prediction.setPredictionId(UUID.randomUUID().toString()); prediction.setPatientId(patientId);
        prediction.setModelType((String) response.get("modelType")); prediction.setModelVersion((String) response.get("modelVersion"));
        prediction.setRiskScore(number(response.get("riskScore"))); prediction.setRiskPercentage(number(response.get("riskPercentage")));
        prediction.setRiskCategory((String) response.get("riskCategory")); prediction.setConfidence(number(response.get("confidence")));
        prediction.setPredictionTimestamp(OffsetDateTime.parse((String) response.get("predictionTimestamp")).toLocalDateTime());
        prediction.setExplanation((List<Map<String, Object>>) response.get("explanation")); prediction.setImputedFeatures(payload.imputedFeatures());
        prediction.setShapOutputSpace((String) response.get("shapOutputSpace"));
        prediction.setShapBaseValue(number(response.get("shapBaseValue")));
        prediction.setShapExplainedValue(number(response.get("shapExplainedValue")));
        prediction.setShapConsistencyError(number(response.get("shapConsistencyError")));
        RiskPrediction saved = predictionRepository.save(prediction);
        HealthTwin twin = twinService.getTwinByPatientId(patientId);
        Map<String, Object> riskPredictions = twin.getRiskPredictions() == null
                ? new HashMap<>() : new HashMap<>(twin.getRiskPredictions());
        riskPredictions.put(modelType, Map.of(
                "predictionId", saved.getPredictionId(), "riskPercentage", saved.getRiskPercentage(),
                "riskCategory", saved.getRiskCategory(), "modelVersion", saved.getModelVersion(),
                "timestamp", saved.getPredictionTimestamp().toString()));
        twinService.updateTwin(patientId, Map.of("riskPredictions", riskPredictions));
        auditService.record(actor, "CLINICAL", "AI_PREDICTION_CREATED", "RiskPrediction", saved.getPredictionId(), patientId, "SUCCESS");
        syncModels();
        return saved;
    }

    public List<RiskPrediction> predictions(String patientId) { return predictionRepository.findByPatientIdOrderByPredictionTimestampDesc(patientId); }
    public RiskPrediction explanation(String predictionId) { return predictionRepository.findByPredictionId(predictionId).orElseThrow(() -> new IllegalArgumentException("Prediction not found: " + predictionId)); }
    public List<AIModel> models() { syncModels(); return modelRepository.findAll(); }
    public Map<String, Object> federatedStatus() { return aiClient.federatedStatus(); }

    private void syncModels() {
        Object raw = aiClient.models();
        if (!(raw instanceof List<?> records)) return;
        for (Object item : records) {
            if (!(item instanceof Map<?, ?> record)) continue;
            AIModel model = new AIModel();
            model.setModelId((String) record.get("modelId")); model.setModelType((String) record.get("modelType"));
            model.setVersion((String) record.get("version")); model.setAlgorithm((String) record.get("algorithm"));
            model.setTrainingMethod((String) record.get("trainingMethod")); model.setDatasetVersion((String) record.get("datasetVersion"));
            model.setStatus((String) record.get("status")); model.setMetrics((Map<String, Object>) record.get("metrics")); model.setRegisteredAt(LocalDateTime.now());
            modelRepository.findAll().stream().filter(existing -> existing.getModelId().equals(model.getModelId())).findFirst().ifPresent(existing -> model.setId(existing.getId()));
            modelRepository.save(model);
        }
    }

    private Double number(Object value) { return value instanceof Number number ? number.doubleValue() : null; }
}
