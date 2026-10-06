package com.medisphere.controller;

import com.medisphere.model.Alert;
import com.medisphere.service.AlertService;
import com.medisphere.repository.VitalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService service;
    private final VitalRepository vitalRepository;

    public AlertController(AlertService service, VitalRepository vitalRepository) {
        this.service = service;
        this.vitalRepository = vitalRepository;
    }

    @GetMapping
    public ResponseEntity<List<Alert>> active() { return ResponseEntity.ok(service.active()); }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Alert>> byPatient(@PathVariable String patientId) { return ResponseEntity.ok(service.byPatient(patientId)); }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> statistics() { return ResponseEntity.ok(service.statistics()); }

    @GetMapping("/events")
    public ResponseEntity<?> recentEvents() { return ResponseEntity.ok(vitalRepository.findTop50ByOrderByTimestampDesc()); }

    @PostMapping("/{alertId}/acknowledge")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Alert> acknowledge(@PathVariable String alertId, Principal principal) {
        return ResponseEntity.ok(service.acknowledge(alertId, principal == null ? "system" : principal.getName()));
    }

    @PostMapping("/{alertId}/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Alert> updateStatus(@PathVariable String alertId, @PathVariable String status, Principal principal) {
        return ResponseEntity.ok(service.updateStatus(alertId, status.toUpperCase(), principal == null ? "system" : principal.getName()));
    }
}