package com.medisphere.controller;

import com.medisphere.model.Vital;
import com.medisphere.service.VitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/vitals")
public class VitalController {
    private final VitalService service;

    public VitalController(VitalService service) { this.service = service; }

    @GetMapping("/{patientId}")
    public ResponseEntity<List<Vital>> getByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(service.findByPatient(patientId));
    }

    @PostMapping
    public ResponseEntity<Vital> ingest(@RequestBody Vital vital, Principal principal) {
        return ResponseEntity.ok(service.ingest(vital, principal == null ? "system" : principal.getName()));
    }
}