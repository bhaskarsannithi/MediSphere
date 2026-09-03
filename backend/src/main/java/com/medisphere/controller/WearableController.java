package com.medisphere.controller;

import com.medisphere.service.VitalStreamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wearables")
public class WearableController {
    private final VitalStreamService streamService;

    public WearableController(VitalStreamService streamService) { this.streamService = streamService; }

    @PostMapping("/simulate")
    public ResponseEntity<Map<String, Object>> simulate(@RequestBody Map<String, Object> event) {
        if (event.get("patientId") == null || event.get("vitalType") == null || event.get("value") == null || event.get("unit") == null) {
            throw new IllegalArgumentException("patientId, vitalType, value and unit are required");
        }
        streamService.publish(Map.of(
                "patientId", event.get("patientId"),
                "type", event.get("vitalType"),
                "value", event.get("value"),
                "unit", event.get("unit"),
                "timestamp", event.getOrDefault("timestamp", java.time.LocalDateTime.now().toString()),
                "source", event.getOrDefault("source", "SYNTHETIC_WEARABLE")
        ));
        return ResponseEntity.accepted().body(Map.of("status", "PUBLISHED", "topic", "vital-signs"));
    }
}