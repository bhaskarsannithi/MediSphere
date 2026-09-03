package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "fhir_resources")
public class FHIRResource {
    @Id
    private String id;
    @Indexed(unique = true)
    private String resourceId;
    @Indexed
    private String patientId;
    private String resourceType;
    private String fhirResourceId;
    private String resourceJson;
    private String validationStatus;
    private String source;
    private LocalDateTime createdAt;

    public FHIRResource() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getFhirResourceId() { return fhirResourceId; }
    public void setFhirResourceId(String fhirResourceId) { this.fhirResourceId = fhirResourceId; }
    public String getResourceJson() { return resourceJson; }
    public void setResourceJson(String resourceJson) { this.resourceJson = resourceJson; }
    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
