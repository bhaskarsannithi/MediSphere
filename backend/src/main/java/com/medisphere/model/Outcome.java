package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "outcomes")
@CompoundIndex(name = "outcome_patient_plan_idx", def = "{ 'patientId': 1, 'carePlanId': 1, 'measurementDate': -1 }")
public class Outcome {
    @Id private String id;
    private String outcomeId;
    private String patientId;
    private String carePlanId;
    private String metric;
    private Double baselineValue;
    private Double currentValue;
    private Double targetValue;
    private String unit;
    private LocalDate measurementDate;
    private LocalDate targetDate;
    private Double change;
    private Double progressPercentage;
    private String status;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOutcomeId() { return outcomeId; }
    public void setOutcomeId(String outcomeId) { this.outcomeId = outcomeId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getCarePlanId() { return carePlanId; }
    public void setCarePlanId(String carePlanId) { this.carePlanId = carePlanId; }
    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }
    public Double getBaselineValue() { return baselineValue; }
    public void setBaselineValue(Double baselineValue) { this.baselineValue = baselineValue; }
    public Double getCurrentValue() { return currentValue; }
    public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }
    public Double getTargetValue() { return targetValue; }
    public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public LocalDate getMeasurementDate() { return measurementDate; }
    public void setMeasurementDate(LocalDate measurementDate) { this.measurementDate = measurementDate; }
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public Double getChange() { return change; }
    public void setChange(Double change) { this.change = change; }
    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}