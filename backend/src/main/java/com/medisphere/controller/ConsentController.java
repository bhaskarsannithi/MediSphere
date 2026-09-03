package com.medisphere.controller;

import com.medisphere.model.Consent;
import com.medisphere.service.ConsentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/consents")
public class ConsentController {
    private final ConsentService service;

    public ConsentController(ConsentService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Consent>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<List<Consent>> getByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(service.findByPatient(patientId));
    }

    @PostMapping
    public ResponseEntity<Consent> create(@RequestBody Consent consent, Principal principal) {
        return ResponseEntity.ok(service.create(consent, principal == null ? "system" : principal.getName()));
    }

    @PostMapping("/{consentId}/revoke")
    public ResponseEntity<Consent> revoke(@PathVariable String consentId, Principal principal) {
        return ResponseEntity.ok(service.revoke(consentId, principal == null ? "system" : principal.getName()));
    }
}