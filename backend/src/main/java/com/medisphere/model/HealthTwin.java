package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "health_twins")
public class HealthTwin {
    @Id
    private String id;
    @Indexed(unique = true)
    private String twinId;
    @Indexed(unique = true)
    private String patientId;
    private Map<String, Object> demographics;
    private Map<String, Object> currentVitals;
    private List<Map<String, Object>> recentLabs;
    private List<String> fhirResources;
    private String consentStatus;
    private Integer completeness;
    private List<String> connectedSources;
    private LocalDateTime lastUpdated;

    public HealthTwin() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTwinId() { return twinId; }
    public void setTwinId(String twinId) { this.twinId = twinId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public Map<String, Object> getDemographics() { return demographics; }
    public void setDemographics(Map<String, Object> demographics) { this.demographics = demographics; }
    public Map<String, Object> getCurrentVitals() { return currentVitals; }
    public void setCurrentVitals(Map<String, Object> currentVitals) { this.currentVitals = currentVitals; }
    public List<Map<String, Object>> getRecentLabs() { return recentLabs; }
    public void setRecentLabs(List<Map<String, Object>> recentLabs) { this.recentLabs = recentLabs; }
    public List<String> getFhirResources() { return fhirResources; }
    public void setFhirResources(List<String> fhirResources) { this.fhirResources = fhirResources; }
    public String getConsentStatus() { return consentStatus; }
    public void setConsentStatus(String consentStatus) { this.consentStatus = consentStatus; }
    public Integer getCompleteness() { return completeness; }
    public void setCompleteness(Integer completeness) { this.completeness = completeness; }
    public List<String> getConnectedSources() { return connectedSources; }
    public void setConnectedSources(List<String> connectedSources) { this.connectedSources = connectedSources; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
