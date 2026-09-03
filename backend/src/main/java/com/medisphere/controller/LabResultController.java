package com.medisphere.controller;

import com.medisphere.model.LabResult;
import com.medisphere.service.LabResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/labs")
public class LabResultController {
    private final LabResultService service;

    public LabResultController(LabResultService service) { this.service = service; }

    @GetMapping("/{patientId}")
    public ResponseEntity<List<LabResult>> getByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(service.findByPatient(patientId));
    }

    @PostMapping
    public ResponseEntity<LabResult> ingest(@RequestBody LabResult lab, Principal principal) {
        return ResponseEntity.ok(service.ingest(lab, principal == null ? "system" : principal.getName()));
    }
}