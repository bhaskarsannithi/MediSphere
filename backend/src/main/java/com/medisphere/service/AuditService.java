package com.medisphere.service;

import com.medisphere.model.AuditLog;
import com.medisphere.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {
    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditLog record(String userId, String role, String action, String resourceType,
                           String resourceId, String patientId, String result) {
        AuditLog log = new AuditLog();
        log.setAuditId(UUID.randomUUID().toString());
        log.setTimestamp(LocalDateTime.now());
        log.setUserId(userId);
        log.setRole(role);
        log.setAction(action);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setPatientId(patientId);
        log.setResult(result);
        return repository.save(log);
    }

    public List<AuditLog> findAll() {
        return repository.findAll();
    }
}
