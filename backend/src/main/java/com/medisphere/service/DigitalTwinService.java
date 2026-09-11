package com.medisphere.service;

import com.medisphere.model.HealthTwin;
import com.medisphere.model.Patient;
import com.medisphere.repository.HealthTwinRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DigitalTwinService {

    private final HealthTwinRepository healthTwinRepository;

    public DigitalTwinService(HealthTwinRepository healthTwinRepository) {
        this.healthTwinRepository = healthTwinRepository;
    }

    public List<HealthTwin> getAllTwins() {
        return healthTwinRepository.findAll();
    }

    public HealthTwin getTwinByPatientId(String patientId) {
        return healthTwinRepository.findByPatientId(patientId)
                .orElseGet(() -> createTwin(patientId));
    }

    public HealthTwin createTwin(String patientId) {
        HealthTwin twin = new HealthTwin();
        twin.setTwinId("TWIN-" + patientId);
        twin.setPatientId(patientId);
        twin.setDemographics(Map.of("patientId", patientId, "status", "INITIALIZED"));
        twin.setCurrentVitals(Map.of());
        twin.setRecentLabs(new ArrayList<>());
        twin.setFhirResources(new ArrayList<>());
        twin.setConsentStatus("PENDING");
        twin.setCompleteness(0);
        twin.setConnectedSources(new ArrayList<>(List.of("EHR", "LAB", "WEARABLE")));
        twin.setRiskPredictions(new java.util.HashMap<>());
        twin.setLastUpdated(LocalDateTime.now());
        return healthTwinRepository.save(twin);
    }

    public HealthTwin updateTwin(String patientId, Map<String, Object> updates) {
        HealthTwin twin = healthTwinRepository.findByPatientId(patientId).orElseGet(() -> createTwin(patientId));
        if (updates.containsKey("demographics")) {
            twin.setDemographics((Map<String, Object>) updates.get("demographics"));
        }
        if (updates.containsKey("currentVitals")) {
            twin.setCurrentVitals((Map<String, Object>) updates.get("currentVitals"));
        }
        if (updates.containsKey("recentLabs")) {
            twin.setRecentLabs((List<Map<String, Object>>) updates.get("recentLabs"));
        }
        if (updates.containsKey("fhirResources")) {
            twin.setFhirResources((List<String>) updates.get("fhirResources"));
        }
        if (updates.containsKey("consentStatus")) {
            twin.setConsentStatus((String) updates.get("consentStatus"));
        }
        if (updates.containsKey("connectedSources")) {
            twin.setConnectedSources((List<String>) updates.get("connectedSources"));
        }
        if (updates.containsKey("riskPredictions")) {
            twin.setRiskPredictions((Map<String, Object>) updates.get("riskPredictions"));
        }
        twin.setCompleteness(calculateCompleteness(twin));
        twin.setLastUpdated(LocalDateTime.now());
        return healthTwinRepository.save(twin);
    }

    public int calculateCompleteness(HealthTwin twin) {
        int demographics = twin.getDemographics() != null && !twin.getDemographics().isEmpty() ? 25 : 0;
        int vitals = twin.getCurrentVitals() != null && !twin.getCurrentVitals().isEmpty() ? 25 : 0;
        int labs = twin.getRecentLabs() != null && !twin.getRecentLabs().isEmpty() ? 20 : 0;
        int fhir = twin.getFhirResources() != null && !twin.getFhirResources().isEmpty() ? 20 : 0;
        int consent = twin.getConsentStatus() != null && !twin.getConsentStatus().isBlank() ? 10 : 0;

        return demographics + vitals + labs + fhir + consent;
    }
}
