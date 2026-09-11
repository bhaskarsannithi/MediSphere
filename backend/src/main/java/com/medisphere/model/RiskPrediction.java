package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "risk_predictions")
@CompoundIndex(name = "risk_prediction_patient_timestamp_idx", def = "{ 'patientId': 1, 'predictionTimestamp': -1 }")
public class RiskPrediction {
    @Id private String id;
    private String predictionId;
    private String patientId;
    private String modelType;
    private String modelVersion;
    private Double riskScore;
    private Double riskPercentage;
    private String riskCategory;
    private Double confidence;
    private LocalDateTime predictionTimestamp;
    private List<Map<String, Object>> explanation;
    private String shapOutputSpace;
    private Double shapBaseValue;
    private Double shapExplainedValue;
    private Double shapConsistencyError;
    private List<String> imputedFeatures;
    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getPredictionId() { return predictionId; } public void setPredictionId(String predictionId) { this.predictionId = predictionId; }
    public String getPatientId() { return patientId; } public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getModelType() { return modelType; } public void setModelType(String modelType) { this.modelType = modelType; }
    public String getModelVersion() { return modelVersion; } public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public Double getRiskScore() { return riskScore; } public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    public Double getRiskPercentage() { return riskPercentage; } public void setRiskPercentage(Double riskPercentage) { this.riskPercentage = riskPercentage; }
    public String getRiskCategory() { return riskCategory; } public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }
    public Double getConfidence() { return confidence; } public void setConfidence(Double confidence) { this.confidence = confidence; }
    public LocalDateTime getPredictionTimestamp() { return predictionTimestamp; } public void setPredictionTimestamp(LocalDateTime predictionTimestamp) { this.predictionTimestamp = predictionTimestamp; }
    public List<Map<String, Object>> getExplanation() { return explanation; } public void setExplanation(List<Map<String, Object>> explanation) { this.explanation = explanation; }
    public String getShapOutputSpace() { return shapOutputSpace; } public void setShapOutputSpace(String shapOutputSpace) { this.shapOutputSpace = shapOutputSpace; }
    public Double getShapBaseValue() { return shapBaseValue; } public void setShapBaseValue(Double shapBaseValue) { this.shapBaseValue = shapBaseValue; }
    public Double getShapExplainedValue() { return shapExplainedValue; } public void setShapExplainedValue(Double shapExplainedValue) { this.shapExplainedValue = shapExplainedValue; }
    public Double getShapConsistencyError() { return shapConsistencyError; } public void setShapConsistencyError(Double shapConsistencyError) { this.shapConsistencyError = shapConsistencyError; }
    public List<String> getImputedFeatures() { return imputedFeatures; } public void setImputedFeatures(List<String> imputedFeatures) { this.imputedFeatures = imputedFeatures; }
}
