package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "alerts")
@CompoundIndex(name = "alerts_patient_created_idx", def = "{ 'patientId': 1, 'createdAt': -1 }")
public class Alert {
    @Id
    private String id;
    private String alertId;
    private String patientId;
    private String vitalType;
    private Double observedValue;
    private Double baselineValue;
    private Double anomalyScore;
    private String detectionMethod;
    private String alertType;
    private String severity;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime eventReceivedAt;
    private LocalDateTime detectedAt;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String assignedTo;
    private String notificationStatus;
    private String source;
    private String correlationId;
    private Integer suppressedCount = 0;
    private Long processingLatencyMs;
    private Long acknowledgementLatencyMs;

    public Alert() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getVitalType() { return vitalType; }
    public void setVitalType(String vitalType) { this.vitalType = vitalType; }
    public Double getObservedValue() { return observedValue; }
    public void setObservedValue(Double observedValue) { this.observedValue = observedValue; }
    public Double getBaselineValue() { return baselineValue; }
    public void setBaselineValue(Double baselineValue) { this.baselineValue = baselineValue; }
    public Double getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(Double anomalyScore) { this.anomalyScore = anomalyScore; }
    public String getDetectionMethod() { return detectionMethod; }
    public void setDetectionMethod(String detectionMethod) { this.detectionMethod = detectionMethod; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getEventReceivedAt() { return eventReceivedAt; }
    public void setEventReceivedAt(LocalDateTime eventReceivedAt) { this.eventReceivedAt = eventReceivedAt; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getNotificationStatus() { return notificationStatus; }
    public void setNotificationStatus(String notificationStatus) { this.notificationStatus = notificationStatus; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public Integer getSuppressedCount() { return suppressedCount; }
    public void setSuppressedCount(Integer suppressedCount) { this.suppressedCount = suppressedCount; }
    public Long getProcessingLatencyMs() { return processingLatencyMs; }
    public void setProcessingLatencyMs(Long processingLatencyMs) { this.processingLatencyMs = processingLatencyMs; }
    public Long getAcknowledgementLatencyMs() { return acknowledgementLatencyMs; }
    public void setAcknowledgementLatencyMs(Long acknowledgementLatencyMs) { this.acknowledgementLatencyMs = acknowledgementLatencyMs; }
}