package com.medisphere.service;

import com.medisphere.model.LabResult;
import com.medisphere.model.Patient;
import com.medisphere.model.Vital;
import com.medisphere.repository.LabResultRepository;
import com.medisphere.repository.VitalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AIFeatureService {
    private static final Map<String, Double> DEFAULTS = Map.ofEntries(
            Map.entry("bmi", 27.0), Map.entry("systolic_bp", 122.0), Map.entry("diastolic_bp", 78.0),
            Map.entry("heart_rate", 75.0), Map.entry("hba1c", 5.7), Map.entry("glucose", 100.0),
            Map.entry("cholesterol", 190.0), Map.entry("smoking", 0.0), Map.entry("diabetes", 0.0),
            Map.entry("family_history", 0.0), Map.entry("previous_cvd", 0.0), Map.entry("diabetes_duration", 0.0),
            Map.entry("kidney_indicator", 0.0));
    private final VitalRepository vitalRepository;
    private final LabResultRepository labResultRepository;

    public AIFeatureService(VitalRepository vitalRepository, LabResultRepository labResultRepository) {
        this.vitalRepository = vitalRepository;
        this.labResultRepository = labResultRepository;
    }

    public FeaturePayload build(Patient patient) {
        Map<String, Double> features = new LinkedHashMap<>();
        List<String> imputed = new ArrayList<>();
        features.put("age", (double) (patient.getDateOfBirth() == null ? 50 : Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears()));
        features.put("sex", "male".equalsIgnoreCase(patient.getGender()) ? 1.0 : 0.0);
        Map<String, Double> vitalValues = latestVitals(patient.getPatientId());
        Map<String, Double> labValues = latestLabs(patient.getPatientId());
        for (Map.Entry<String, Double> entry : DEFAULTS.entrySet()) {
            Double value = vitalValues.get(entry.getKey());
            if (value == null) value = labValues.get(entry.getKey());
            if (value == null) {
                value = entry.getValue();
                imputed.add(entry.getKey());
            }
            features.put(entry.getKey(), value);
        }
        return new FeaturePayload(features, imputed);
    }

    private Map<String, Double> latestVitals(String patientId) {
        Map<String, Double> result = new LinkedHashMap<>();
        Map<String, LocalDateTime> timestamps = new LinkedHashMap<>();
        for (Vital vital : vitalRepository.findByPatientId(patientId)) {
            if (vital.getValue() == null) continue;
            String key = switch (normalize(vital.getType())) {
                case "HEART_RATE" -> "heart_rate";
                case "BLOOD_PRESSURE_SYSTOLIC", "SYSTOLIC_BP" -> "systolic_bp";
                case "BLOOD_PRESSURE_DIASTOLIC", "DIASTOLIC_BP" -> "diastolic_bp";
                default -> null;
            };
            if (key != null && isNewer(vital.getTimestamp(), timestamps.get(key))) {
                result.put(key, vital.getValue());
                timestamps.put(key, vital.getTimestamp());
            }
        }
        return result;
    }

    private Map<String, Double> latestLabs(String patientId) {
        Map<String, Double> result = new LinkedHashMap<>();
        Map<String, LocalDateTime> timestamps = new LinkedHashMap<>();
        for (LabResult lab : labResultRepository.findByPatientId(patientId)) {
            if (lab.getValue() == null) continue;
            try {
                String key = switch (normalize(lab.getTestName())) {
                    case "HBA1C", "HBA1C_PERCENT" -> "hba1c";
                    case "GLUCOSE", "FASTING_GLUCOSE" -> "glucose";
                    case "CHOLESTEROL", "TOTAL_CHOLESTEROL" -> "cholesterol";
                    default -> null;
                };
                if (key != null && isNewer(lab.getTimestamp(), timestamps.get(key))) {
                    result.put(key, Double.parseDouble(lab.getValue()));
                    timestamps.put(key, lab.getTimestamp());
                }
            } catch (NumberFormatException ignored) { }
        }
        return result;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
    }

    private boolean isNewer(LocalDateTime candidate, LocalDateTime current) {
        return current == null || (candidate != null && candidate.isAfter(current));
    }

    public record FeaturePayload(Map<String, Double> features, List<String> imputedFeatures) { }
}
