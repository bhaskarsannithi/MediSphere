package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "ai_models")
public class AIModel {
    @Id private String id;
    @Indexed(unique = true) private String modelId;
    private String modelType;
    private String version;
    private String algorithm;
    private String trainingMethod;
    private String datasetVersion;
    private String status;
    private Map<String, Object> metrics;
    private LocalDateTime registeredAt;
    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getModelId() { return modelId; } public void setModelId(String modelId) { this.modelId = modelId; }
    public String getModelType() { return modelType; } public void setModelType(String modelType) { this.modelType = modelType; }
    public String getVersion() { return version; } public void setVersion(String version) { this.version = version; }
    public String getAlgorithm() { return algorithm; } public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    public String getTrainingMethod() { return trainingMethod; } public void setTrainingMethod(String trainingMethod) { this.trainingMethod = trainingMethod; }
    public String getDatasetVersion() { return datasetVersion; } public void setDatasetVersion(String datasetVersion) { this.datasetVersion = datasetVersion; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public Map<String, Object> getMetrics() { return metrics; } public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    public LocalDateTime getRegisteredAt() { return registeredAt; } public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}
