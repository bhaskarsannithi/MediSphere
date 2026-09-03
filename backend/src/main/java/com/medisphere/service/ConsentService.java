package com.medisphere.service;

import com.medisphere.model.Consent;
import com.medisphere.repository.ConsentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConsentService {
    private final ConsentRepository repository;
    private final AuditService auditService;

    public ConsentService(ConsentRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public List<Consent> findByPatient(String patientId) {
        return repository.findByPatientId(patientId);
    }

    public List<Consent> findAll() {
        return repository.findAll();
    }

    public Consent create(Consent consent, String actor) {
        if (consent.getPatientId() == null || consent.getPatientId().isBlank()) {
            throw new IllegalArgumentException("patientId is required");
        }
        consent.setConsentId(UUID.randomUUID().toString());
        consent.setStatus(consent.getStatus() == null ? "GRANTED" : consent.getStatus());
        consent.setGrantedAt("GRANTED".equals(consent.getStatus()) ? LocalDateTime.now() : null);
        consent.setCreatedBy(actor);
        consent.setUpdatedAt(LocalDateTime.now());
        Consent saved = repository.save(consent);
        auditService.record(actor, "CLINICAL", "CONSENT_CREATED", "Consent", saved.getConsentId(), saved.getPatientId(), "SUCCESS");
        return saved;
    }

    public Consent revoke(String consentId, String actor) {
        Consent consent = repository.findByConsentId(consentId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found: " + consentId));
        consent.setStatus("REVOKED");
        consent.setRevokedAt(LocalDateTime.now());
        consent.setUpdatedAt(LocalDateTime.now());
        Consent saved = repository.save(consent);
        auditService.record(actor, "CLINICAL", "CONSENT_REVOKED", "Consent", consentId, saved.getPatientId(), "SUCCESS");
        return saved;
    }
}
