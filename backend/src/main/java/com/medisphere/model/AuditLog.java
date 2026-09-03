package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "audit_logs")
@CompoundIndex(name = "audit_patient_timestamp_idx", def = "{ 'patientId': 1, 'timestamp': -1 }")
public class AuditLog {
    @Id
    private String id;
    private String auditId;
    private LocalDateTime timestamp;
    private String userId;
    private String role;
    private String action;
    private String resourceType;
    private String resourceId;
    private String patientId;
    private String result;

    public AuditLog() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
