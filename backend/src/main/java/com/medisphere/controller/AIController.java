package com.medisphere.controller;

import com.medisphere.model.AIModel;
import com.medisphere.model.RiskPrediction;
import com.medisphere.service.AIPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIPredictionService predictionService;
    public AIController(AIPredictionService predictionService) { this.predictionService = predictionService; }

    @PostMapping("/predict/cvd/{patientId}")
    public ResponseEntity<RiskPrediction> predictCvd(@PathVariable String patientId, Authentication authentication) {
        return ResponseEntity.ok(predictionService.predict("CVD", patientId, authentication.getName()));
    }

    @PostMapping("/predict/diabetes/{patientId}")
    public ResponseEntity<RiskPrediction> predictDiabetes(@PathVariable String patientId, Authentication authentication) {
        return ResponseEntity.ok(predictionService.predict("DIABETES_COMPLICATION", patientId, authentication.getName()));
    }

    @GetMapping("/predictions/{patientId}")
    public ResponseEntity<List<RiskPrediction>> predictions(@PathVariable String patientId) {
        return ResponseEntity.ok(predictionService.predictions(patientId));
    }

    @GetMapping("/explanations/{predictionId}")
    public ResponseEntity<RiskPrediction> explanation(@PathVariable String predictionId) {
        return ResponseEntity.ok(predictionService.explanation(predictionId));
    }

    @GetMapping("/models")
    public ResponseEntity<List<AIModel>> models() { return ResponseEntity.ok(predictionService.models()); }

    @GetMapping("/federated/status")
    public ResponseEntity<Map<String, Object>> federatedStatus() { return ResponseEntity.ok(predictionService.federatedStatus()); }
}
