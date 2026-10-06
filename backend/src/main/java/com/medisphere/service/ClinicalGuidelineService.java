package com.medisphere.service;

import com.medisphere.model.CarePlan;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClinicalGuidelineService {
    public Map<String, Object> validate(CarePlan plan) {
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int rulesChecked = 5;
        if (plan.getPatientId() == null || plan.getPatientId().isBlank()) errors.add("Patient information is required");
        if (plan.getGoals() == null || plan.getGoals().isEmpty()) errors.add("At least one measurable goal is required");
        if (plan.getGoals() != null) {
            for (CarePlan.Goal goal : plan.getGoals()) {
                if (goal.getTargetMetric() == null || goal.getTargetMetric().isBlank() || goal.getTargetValue() == null) {
                    errors.add("Every goal must include a target metric and value");
                }
                if (goal.getTargetValue() != null && goal.getTargetValue() < 0) errors.add("Target values cannot be negative");
            }
        }
        if (plan.getMonitoringTasks() == null || plan.getMonitoringTasks().isEmpty()) warnings.add("Add at least one monitoring task for follow-up");
        if (plan.getInterventions() != null) {
            for (CarePlan.Intervention intervention : plan.getInterventions()) {
                if (intervention.getDescription() == null || intervention.getDescription().isBlank()) errors.add("Interventions require a description");
                if ("MEDICATION".equalsIgnoreCase(intervention.getType())) {
                    errors.add("Provider review required before medication-related intervention");
                }
            }
            long unique = plan.getInterventions().stream().map(CarePlan.Intervention::getDescription).distinct().count();
            if (unique != plan.getInterventions().size()) warnings.add("Duplicate interventions should be consolidated");
        }
        if (errors.isEmpty()) recommendations.add("Provider review is still required before activation");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("compliant", errors.isEmpty());
        result.put("warnings", warnings);
        result.put("errors", errors);
        result.put("recommendations", recommendations);
        result.put("rulesChecked", rulesChecked);
        result.put("validationTimestamp", LocalDateTime.now());
        return result;
    }
}