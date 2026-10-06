package com.medisphere.service;

import com.medisphere.model.CarePlan;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClinicalGuidelineServiceTest {
    private final ClinicalGuidelineService service = new ClinicalGuidelineService();

    @Test
    void acceptsMeasurableLifestylePlanWithMonitoring() {
        CarePlan plan = validPlan();
        Map<String, Object> result = service.validate(plan);
        assertTrue((Boolean) result.get("compliant"));
        assertTrue(((List<?>) result.get("errors")).isEmpty());
        assertEquals(5, result.get("rulesChecked"));
    }

    @Test
    void rejectsMedicationInterventionUntilProviderReview() {
        CarePlan plan = validPlan();
        CarePlan.Intervention medication = new CarePlan.Intervention();
        medication.setType("MEDICATION");
        medication.setDescription("Medication recommendation");
        plan.setInterventions(List.of(medication));
        Map<String, Object> result = service.validate(plan);
        assertFalse((Boolean) result.get("compliant"));
        assertTrue(((List<?>) result.get("errors")).contains("Provider review required before medication-related intervention"));
    }

    private CarePlan validPlan() {
        CarePlan plan = new CarePlan();
        plan.setPatientId("SYNTHETIC-1");
        CarePlan.Goal goal = new CarePlan.Goal();
        goal.setTargetMetric("BLOOD_PRESSURE_SYSTOLIC");
        goal.setTargetValue(130.0);
        plan.setGoals(List.of(goal));
        CarePlan.Intervention intervention = new CarePlan.Intervention();
        intervention.setType("LIFESTYLE");
        intervention.setDescription("Clinician-reviewed lifestyle plan");
        plan.setInterventions(List.of(intervention));
        CarePlan.MonitoringTask task = new CarePlan.MonitoringTask();
        task.setMetric("BLOOD_PRESSURE_SYSTOLIC");
        task.setFrequency("Daily");
        plan.setMonitoringTasks(List.of(task));
        return plan;
    }
}