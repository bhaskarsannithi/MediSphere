package com.medisphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medisphere.model.AdherenceRecord;
import com.medisphere.model.CarePlan;
import com.medisphere.model.Outcome;
import com.medisphere.service.CarePlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/careplans")
public class CarePlanController {
    private final CarePlanService service;
    private final ObjectMapper objectMapper;
    public CarePlanController(CarePlanService service, ObjectMapper objectMapper) { this.service = service; this.objectMapper = objectMapper; }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<CarePlan> generate(@RequestBody Map<String, String> request, Principal principal) { return ResponseEntity.ok(service.generate(request.get("patientId"), principal.getName())); }
    @GetMapping("/statistics") public ResponseEntity<Map<String, Object>> statistics() { return ResponseEntity.ok(service.statistics()); }
    @GetMapping("/{id}") public ResponseEntity<CarePlan> get(@PathVariable String id) { return ResponseEntity.ok(service.get(id)); }
    @GetMapping("/patient/{patientId}") public ResponseEntity<List<CarePlan>> byPatient(@PathVariable String patientId) { return ResponseEntity.ok(service.byPatient(patientId)); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')") public ResponseEntity<CarePlan> update(@PathVariable String id, @RequestBody CarePlan changes, Principal principal) { return ResponseEntity.ok(service.update(id, changes, principal.getName())); }
    @PostMapping("/{id}/validate") @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')") public ResponseEntity<CarePlan> validate(@PathVariable String id) { return ResponseEntity.ok(service.validate(id)); }
    @PostMapping("/{id}/approve") @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')") public ResponseEntity<CarePlan> approve(@PathVariable String id, @RequestBody(required = false) Map<String, String> request, Principal principal) { return ResponseEntity.ok(service.review(id, "APPROVE", principal.getName(), comment(request), null)); }
    @PostMapping("/{id}/reject") @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')") public ResponseEntity<CarePlan> reject(@PathVariable String id, @RequestBody(required = false) Map<String, String> request, Principal principal) { return ResponseEntity.ok(service.review(id, "REJECT", principal.getName(), comment(request), null)); }
    @PostMapping("/{id}/modify") @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')") public ResponseEntity<CarePlan> modify(@PathVariable String id, @RequestBody Map<String, Object> request, Principal principal) { return ResponseEntity.ok(service.review(id, "MODIFY", principal.getName(), (String) request.get("comments"), objectMapper.convertValue(request.get("interventions"), objectMapper.getTypeFactory().constructCollectionType(List.class, CarePlan.Intervention.class)))); }
    @PostMapping("/{id}/activate") @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')") public ResponseEntity<CarePlan> activate(@PathVariable String id, Principal principal) { return ResponseEntity.ok(service.review(id, "APPROVE", principal.getName(), "Activated by provider", null)); }
    @PostMapping("/{id}/adherence") public ResponseEntity<AdherenceRecord> adherence(@PathVariable String id, @RequestBody AdherenceRecord record) { record.setCarePlanId(id); return ResponseEntity.ok(service.recordAdherence(record)); }
    @PutMapping("/adherence/{adherenceId}") public ResponseEntity<AdherenceRecord> updateAdherence(@PathVariable String adherenceId, @RequestBody AdherenceRecord record) { return ResponseEntity.ok(service.updateAdherence(adherenceId, record)); }
    @GetMapping("/{id}/adherence") public ResponseEntity<List<AdherenceRecord>> adherence(@PathVariable String id) { return ResponseEntity.ok(service.adherence(id)); }
    @GetMapping("/adherence/patient/{patientId}") public ResponseEntity<List<AdherenceRecord>> adherenceByPatient(@PathVariable String patientId) { return ResponseEntity.ok(service.adherenceByPatient(patientId)); }
    @GetMapping("/{id}/adherence/statistics") public ResponseEntity<Map<String, Object>> adherenceStats(@PathVariable String id) { return ResponseEntity.ok(service.adherenceStats(id)); }
    @PostMapping("/{id}/outcomes") public ResponseEntity<Outcome> outcome(@PathVariable String id, @RequestBody Outcome outcome) { outcome.setCarePlanId(id); return ResponseEntity.ok(service.saveOutcome(outcome)); }
    @PutMapping("/outcomes/{outcomeId}") public ResponseEntity<Outcome> updateOutcome(@PathVariable String outcomeId, @RequestBody Outcome outcome) { return ResponseEntity.ok(service.updateOutcome(outcomeId, outcome)); }
    @GetMapping("/{id}/outcomes") public ResponseEntity<List<Outcome>> outcomes(@PathVariable String id) { return ResponseEntity.ok(service.outcomes(id)); }
    private String comment(Map<String, String> request) { return request == null ? null : request.get("comments"); }
}