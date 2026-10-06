package com.medisphere.service;

import com.medisphere.model.Vital;
import com.medisphere.repository.PatientRepository;
import com.medisphere.repository.VitalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VitalService {
    private final VitalRepository repository;
    private final PatientRepository patientRepository;
    private final DigitalTwinService twinService;
    private final AuditService auditService;
    private final AlertService alertService;

    public VitalService(VitalRepository repository, PatientRepository patientRepository,
                        DigitalTwinService twinService, AuditService auditService, AlertService alertService) {
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.twinService = twinService;
        this.auditService = auditService;
        this.alertService = alertService;
    }

    public List<Vital> findByPatient(String patientId) {
        return repository.findByPatientId(patientId);
    }

    public Vital ingest(Vital vital, String actor) {
        validate(vital);
        if (!patientRepository.existsByPatientId(vital.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + vital.getPatientId());
        }
        vital.setType(vital.getType().toUpperCase());
        if (vital.getPatientId() == null || vital.getType() == null || vital.getValue() == null || vital.getUnit() == null) {
            throw new IllegalArgumentException("patientId, type, value and unit are required");
        }
        vital.setVitalId(vital.getVitalId() == null ? UUID.randomUUID().toString() : vital.getVitalId());
        vital.setTimestamp(vital.getTimestamp() == null ? LocalDateTime.now() : vital.getTimestamp());
        vital.setValidationStatus("VALID");
        Vital saved = repository.save(vital);
        twinService.updateTwin(vital.getPatientId(), java.util.Map.of(
                "currentVitals", java.util.Map.of(vital.getType(), java.util.Map.of("value", vital.getValue(), "unit", vital.getUnit(), "timestamp", vital.getTimestamp(), "source", vital.getSource()))));
        auditService.record(actor, "CLINICAL", "VITAL_INGESTED", "Vital", saved.getVitalId(), saved.getPatientId(), "SUCCESS");
        alertService.evaluate(saved, LocalDateTime.now());
        return saved;
    }

    private void validate(Vital vital) {
        if (vital == null || vital.getPatientId() == null || vital.getPatientId().isBlank()
                || vital.getType() == null || vital.getType().isBlank() || vital.getValue() == null
                || vital.getUnit() == null || vital.getUnit().isBlank()) {
            throw new IllegalArgumentException("patientId, type, value and unit are required");
        }
        String type = vital.getType().toUpperCase();
        double value = vital.getValue();
        boolean valid = switch (type) {
            case "HEART_RATE" -> value >= 20 && value <= 250;
            case "BLOOD_PRESSURE_SYSTOLIC" -> value >= 50 && value <= 250;
            case "BLOOD_PRESSURE_DIASTOLIC" -> value >= 30 && value <= 150;
            case "SPO2" -> value >= 50 && value <= 100;
            case "TEMPERATURE" -> value >= 25 && value <= 45;
            case "RESPIRATORY_RATE" -> value >= 4 && value <= 60;
            default -> false;
        };
        if (!valid) throw new IllegalArgumentException("Invalid or out-of-range vital: " + vital.getType());
        if (vital.getTimestamp() != null && vital.getTimestamp().isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new IllegalArgumentException("Vital timestamp cannot be in the future");
        }
    }
}
