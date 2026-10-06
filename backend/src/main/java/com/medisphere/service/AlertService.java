package com.medisphere.service;

import com.medisphere.model.Alert;
import com.medisphere.model.Vital;
import com.medisphere.repository.AlertRepository;
import com.medisphere.repository.VitalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AlertService {
    private static final List<String> ACTIVE_STATUSES = List.of("NEW", "ACKNOWLEDGED");
    private final AlertRepository alertRepository;
    private final VitalRepository vitalRepository;
    private final AuditService auditService;
    private final long cooldownMinutes;

    public AlertService(AlertRepository alertRepository, VitalRepository vitalRepository, AuditService auditService,
                        @Value("${medisphere.alerts.cooldown-minutes:15}") long cooldownMinutes) {
        this.alertRepository = alertRepository;
        this.vitalRepository = vitalRepository;
        this.auditService = auditService;
        this.cooldownMinutes = cooldownMinutes;
    }

    public void evaluate(Vital vital, LocalDateTime receivedAt) {
        List<Vital> history = vitalRepository.findByPatientId(vital.getPatientId()).stream()
                .filter(previous -> !vital.getVitalId().equals(previous.getVitalId()))
                .filter(previous -> vital.getType().equalsIgnoreCase(previous.getType()))
                .sorted((left, right) -> right.getTimestamp().compareTo(left.getTimestamp()))
                .limit(20)
                .toList();
        DoubleSummaryStatistics statistics = history.stream().mapToDouble(Vital::getValue).summaryStatistics();
        double baseline = statistics.getCount() == 0 ? vital.getValue() : statistics.getAverage();
        double standardDeviation = history.size() < 2 ? 0 : Math.sqrt(history.stream()
                .mapToDouble(previous -> Math.pow(previous.getValue() - baseline, 2)).average().orElse(0));
        double deviation = Math.abs(vital.getValue() - baseline);
        double anomalyScore = standardDeviation > 0 ? deviation / standardDeviation : deviation / Math.max(Math.abs(baseline) * .1, 1);
        String alertType = vital.getType().toUpperCase();
        String severity = clinicalSeverity(alertType, vital.getValue());
        boolean anomalous = anomalyScore >= 3.0 || severity != null;
        if (!anomalous) return;
        if (severity == null) severity = "MEDIUM";

        LocalDateTime now = LocalDateTime.now();
        Alert alert = alertRepository.findTopByPatientIdAndVitalTypeAndAlertTypeAndStatusInAndCreatedAtAfterOrderByCreatedAtDesc(
                vital.getPatientId(), alertType, alertType, ACTIVE_STATUSES, now.minusMinutes(cooldownMinutes)).orElse(null);
        if (alert != null) {
            alert.setSuppressedCount(alert.getSuppressedCount() == null ? 1 : alert.getSuppressedCount() + 1);
            alertRepository.save(alert);
            return;
        }

        alert = new Alert();
        alert.setAlertId(UUID.randomUUID().toString());
        alert.setPatientId(vital.getPatientId());
        alert.setVitalType(alertType);
        alert.setObservedValue(vital.getValue());
        alert.setBaselineValue(baseline);
        alert.setAnomalyScore(anomalyScore);
        alert.setDetectionMethod(history.size() < 2 ? "CLINICAL_THRESHOLD" : "ROLLING_Z_SCORE_AND_CLINICAL_THRESHOLD");
        alert.setAlertType(alertType);
        alert.setSeverity(severity);
        alert.setMessage(alertType + " is outside the configured monitoring rule (observed " + vital.getValue() + " " + vital.getUnit() + ")");
        alert.setStatus("NEW");
        alert.setCreatedAt(now);
        alert.setEventReceivedAt(receivedAt);
        alert.setDetectedAt(now);
        alert.setAssignedTo(severity.equals("CRITICAL") || severity.equals("HIGH") ? "ROLE_DOCTOR" : "ROLE_NURSE");
        alert.setNotificationStatus("IN_APP");
        alert.setSource(vital.getSource());
        alert.setCorrelationId(vital.getVitalId());
        alert.setProcessingLatencyMs(Math.max(0, Duration.between(receivedAt, now).toMillis()));
        Alert saved = alertRepository.save(alert);
        auditService.record("system", "SYSTEM", "ALERT_CREATED", "Alert", saved.getAlertId(), saved.getPatientId(), "SUCCESS");
    }

    public List<Alert> active() { return alertRepository.findByStatusInOrderByCreatedAtDesc(ACTIVE_STATUSES); }
    public List<Alert> byPatient(String patientId) { return alertRepository.findByPatientIdOrderByCreatedAtDesc(patientId); }

    public Alert acknowledge(String alertId, String actor) {
        Alert alert = alertRepository.findByAlertId(alertId).orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        if (!ACTIVE_STATUSES.contains(alert.getStatus())) throw new IllegalArgumentException("Alert is not active");
        LocalDateTime now = LocalDateTime.now();
        alert.setStatus("ACKNOWLEDGED");
        alert.setAcknowledgedBy(actor);
        alert.setAcknowledgedAt(now);
        alert.setAcknowledgementLatencyMs(Math.max(0, Duration.between(alert.getCreatedAt(), now).toMillis()));
        Alert saved = alertRepository.save(alert);
        auditService.record(actor, "CLINICAL", "ALERT_ACKNOWLEDGED", "Alert", saved.getAlertId(), saved.getPatientId(), "SUCCESS");
        return saved;
    }

    public Alert updateStatus(String alertId, String status, String actor) {
        if (!List.of("RESOLVED", "DISMISSED").contains(status)) throw new IllegalArgumentException("Unsupported alert status");
        Alert alert = alertRepository.findByAlertId(alertId).orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        if (!"ACKNOWLEDGED".equals(alert.getStatus())) throw new IllegalArgumentException("Only acknowledged alerts can be resolved or dismissed");
        alert.setStatus(status);
        alert.setResolvedBy(actor);
        alert.setResolvedAt(LocalDateTime.now());
        Alert saved = alertRepository.save(alert);
        auditService.record(actor, "CLINICAL", "ALERT_" + status, "Alert", saved.getAlertId(), saved.getPatientId(), "SUCCESS");
        return saved;
    }

    public Map<String, Object> statistics() {
        List<Alert> all = alertRepository.findAll();
        long acknowledged = all.stream().filter(alert -> "ACKNOWLEDGED".equals(alert.getStatus())).count();
        double averageAckMs = all.stream().filter(alert -> alert.getAcknowledgementLatencyMs() != null)
                .mapToLong(Alert::getAcknowledgementLatencyMs).average().orElse(0);
        return Map.of("totalAlerts", all.size(), "activeAlerts", all.stream().filter(alert -> ACTIVE_STATUSES.contains(alert.getStatus())).count(),
                "criticalAlerts", all.stream().filter(alert -> "CRITICAL".equals(alert.getSeverity())).count(),
                "acknowledgedAlerts", acknowledged, "anomalyCount", all.stream().filter(alert -> alert.getAnomalyScore() != null).count(), "suppressedAlerts",
                all.stream().mapToInt(alert -> alert.getSuppressedCount() == null ? 0 : alert.getSuppressedCount()).sum(),
                "averageAcknowledgementMs", averageAckMs);
    }

    private String clinicalSeverity(String type, double value) {
        if ("HEART_RATE".equals(type)) {
            if (value >= 140 || value <= 35) return "CRITICAL";
            if (value >= 120 || value <= 45) return "HIGH";
        } else if ("SPO2".equals(type) && value <= 90) return value <= 85 ? "CRITICAL" : "HIGH";
        else if ("TEMPERATURE".equals(type) && (value >= 40 || value <= 34)) return "HIGH";
        else if ("RESPIRATORY_RATE".equals(type) && (value >= 30 || value <= 8)) return "HIGH";
        else if ("BLOOD_PRESSURE_SYSTOLIC".equals(type) && (value >= 180 || value <= 80)) return "HIGH";
        return null;
    }
}