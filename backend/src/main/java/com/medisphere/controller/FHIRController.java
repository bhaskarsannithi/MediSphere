package com.medisphere.controller;

import com.medisphere.model.FHIRResource;
import com.medisphere.model.Patient;
import com.medisphere.service.FHIRService;
import com.medisphere.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fhir")
public class FHIRController {

    private final FHIRService fhirService;
    private final PatientService patientService;

    public FHIRController(FHIRService fhirService, PatientService patientService) {
        this.fhirService = fhirService;
        this.patientService = patientService;
    }

    @PostMapping("/resources")
    public ResponseEntity<?> createFHIRResource(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(fhirService.processFHIRResource(payload));
    }

    @PostMapping("/Patient")
    public ResponseEntity<Patient> createPatient(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(fhirService.createPatientFromFHIR(payload));
    }

    @PostMapping("/Observation")
    public ResponseEntity<?> createObservation(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(fhirService.processFHIRResource(payload));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody Map<String, Object> payload) {
        fhirService.validate(payload);
        return ResponseEntity.ok(Map.of("valid", true, "resourceType", payload.get("resourceType")));
    }

    @GetMapping("/resources/{id}")
    public ResponseEntity<FHIRResource> getResource(@PathVariable String id) {
        return ResponseEntity.ok(fhirService.getResourceById(id));
    }

    @GetMapping
    public ResponseEntity<List<FHIRResource>> getResources(@RequestParam(required = false) String patientId) {
        return ResponseEntity.ok(patientId == null ? fhirService.getAllResources() : fhirService.getResources(patientId));
    }

    @PostMapping("/patients")
    public ResponseEntity<Patient> createPatientFromFHIR(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(fhirService.createPatientFromFHIR(payload));
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<Patient> getFPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

    @GetMapping("/patients/{patientId}/observations")
    public ResponseEntity<List<Map<String, Object>>> getObservations(@PathVariable String patientId) {
        return ResponseEntity.ok(fhirService.getObservations(patientId));
    }
}
