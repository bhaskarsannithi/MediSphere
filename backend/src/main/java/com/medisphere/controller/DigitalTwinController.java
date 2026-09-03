package com.medisphere.controller;

import com.medisphere.model.HealthTwin;
import com.medisphere.service.DigitalTwinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DigitalTwinController {

    private final DigitalTwinService digitalTwinService;

    public DigitalTwinController(DigitalTwinService digitalTwinService) {
        this.digitalTwinService = digitalTwinService;
    }

    @GetMapping("/twins")
    public ResponseEntity<List<HealthTwin>> getAllTwins() {
        return ResponseEntity.ok(digitalTwinService.getAllTwins());
    }

    @GetMapping("/twins/{patientId}")
    public ResponseEntity<HealthTwin> getTwin(@PathVariable String patientId) {
        return ResponseEntity.ok(digitalTwinService.getTwinByPatientId(patientId));
    }

    @PostMapping("/twins/{patientId}/update")
    public ResponseEntity<HealthTwin> updateTwin(@PathVariable String patientId,
                                                   @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(digitalTwinService.updateTwin(patientId, updates));
    }
}
