package com.medisphere.service;

import com.medisphere.model.LabResult;
import com.medisphere.repository.LabResultRepository;
import com.medisphere.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LabResultService {
    private final LabResultRepository repository;
    private final PatientRepository patientRepository;
    private final DigitalTwinService twinService;
    private final AuditService auditService;

    public LabResultService(LabResultRepository repository, PatientRepository patientRepository,
                            DigitalTwinService twinService, AuditService auditService) {
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.twinService = twinService;
        this.auditService = auditService;
    }

    public List<LabResult> findByPatient(String patientId) {
        return repository.findByPatientId(patientId);
    }

    public LabResult ingest(LabResult lab, String actor) {
        if (lab.getPatientId() == null || lab.getTestName() == null || lab.getValue() == null) {
            throw new IllegalArgumentException("patientId, testName and value are required");
        }
        if (!patientRepository.existsByPatientId(lab.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + lab.getPatientId());
        }
        lab.setLabResultId(lab.getLabResultId() == null ? UUID.randomUUID().toString() : lab.getLabResultId());
        lab.setTimestamp(lab.getTimestamp() == null ? LocalDateTime.now() : lab.getTimestamp());
        LabResult saved = repository.save(lab);
        twinService.updateTwin(lab.getPatientId(), java.util.Map.of("recentLabs", List.of(java.util.Map.of(
                "testName", lab.getTestName(), "value", lab.getValue(), "unit", lab.getUnit(), "timestamp", lab.getTimestamp()))));
        auditService.record(actor, "CLINICAL", "LAB_INGESTED", "LabResult", saved.getLabResultId(), saved.getPatientId(), "SUCCESS");
        return saved;
    }
}
