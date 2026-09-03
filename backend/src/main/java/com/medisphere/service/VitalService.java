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

    public VitalService(VitalRepository repository, PatientRepository patientRepository,
                        DigitalTwinService twinService, AuditService auditService) {
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.twinService = twinService;
        this.auditService = auditService;
    }

    public List<Vital> findByPatient(String patientId) {
        return repository.findByPatientId(patientId);
    }

    public Vital ingest(Vital vital, String actor) {
        if (vital.getPatientId() == null || vital.getType() == null || vital.getValue() == null || vital.getUnit() == null) {
            throw new IllegalArgumentException("patientId, type, value and unit are required");
        }
        if (!patientRepository.existsByPatientId(vital.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + vital.getPatientId());
        }
        vital.setVitalId(vital.getVitalId() == null ? UUID.randomUUID().toString() : vital.getVitalId());
        vital.setTimestamp(vital.getTimestamp() == null ? LocalDateTime.now() : vital.getTimestamp());
        vital.setValidationStatus("VALID");
        Vital saved = repository.save(vital);
        twinService.updateTwin(vital.getPatientId(), java.util.Map.of(
                "currentVitals", java.util.Map.of(vital.getType(), java.util.Map.of("value", vital.getValue(), "unit", vital.getUnit(), "timestamp", vital.getTimestamp(), "source", vital.getSource()))));
        auditService.record(actor, "CLINICAL", "VITAL_INGESTED", "Vital", saved.getVitalId(), saved.getPatientId(), "SUCCESS");
        return saved;
    }
}
