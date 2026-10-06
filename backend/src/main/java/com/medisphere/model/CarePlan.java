    package com.medisphere.model;

    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.index.CompoundIndex;
    import org.springframework.data.mongodb.core.mapping.Document;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;

    @Document(collection = "careplans")
    @CompoundIndex(name = "careplan_patient_status_idx", def = "{ 'patientId': 1, 'status': 1, 'createdAt': -1 }")
    public class CarePlan {
        @Id private String id;
        private String carePlanId;
        private String patientId;
        private String title;
        private String description;
        private List<Goal> goals = new ArrayList<>();
        private List<Intervention> interventions = new ArrayList<>();
        private List<MonitoringTask> monitoringTasks = new ArrayList<>();
        private List<ExpectedOutcome> expectedOutcomes = new ArrayList<>();
        private Map<String, Object> riskInformation;
        private Map<String, Object> guidelineValidation;
        private String generatedBy;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String reviewedBy;
        private String reviewComments;
        private LocalDateTime reviewedAt;
        private List<String> timeline = new ArrayList<>();

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getCarePlanId() { return carePlanId; }
        public void setCarePlanId(String carePlanId) { this.carePlanId = carePlanId; }
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<Goal> getGoals() { return goals; }
        public void setGoals(List<Goal> goals) { this.goals = goals; }
        public List<Intervention> getInterventions() { return interventions; }
        public void setInterventions(List<Intervention> interventions) { this.interventions = interventions; }
        public List<MonitoringTask> getMonitoringTasks() { return monitoringTasks; }
        public void setMonitoringTasks(List<MonitoringTask> monitoringTasks) { this.monitoringTasks = monitoringTasks; }
        public List<ExpectedOutcome> getExpectedOutcomes() { return expectedOutcomes; }
        public void setExpectedOutcomes(List<ExpectedOutcome> expectedOutcomes) { this.expectedOutcomes = expectedOutcomes; }
        public Map<String, Object> getRiskInformation() { return riskInformation; }
        public void setRiskInformation(Map<String, Object> riskInformation) { this.riskInformation = riskInformation; }
        public Map<String, Object> getGuidelineValidation() { return guidelineValidation; }
        public void setGuidelineValidation(Map<String, Object> guidelineValidation) { this.guidelineValidation = guidelineValidation; }
        public String getGeneratedBy() { return generatedBy; }
        public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public String getReviewedBy() { return reviewedBy; }
        public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
        public String getReviewComments() { return reviewComments; }
        public void setReviewComments(String reviewComments) { this.reviewComments = reviewComments; }
        public LocalDateTime getReviewedAt() { return reviewedAt; }
        public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
        public List<String> getTimeline() { return timeline; }
        public void setTimeline(List<String> timeline) { this.timeline = timeline; }

        public static class Goal {
            private String description;
            private String targetMetric;
            private Double targetValue;
            private String targetUnit;
            private LocalDate targetDate;
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
            public String getTargetMetric() { return targetMetric; }
            public void setTargetMetric(String targetMetric) { this.targetMetric = targetMetric; }
            public Double getTargetValue() { return targetValue; }
            public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }
            public String getTargetUnit() { return targetUnit; }
            public void setTargetUnit(String targetUnit) { this.targetUnit = targetUnit; }
            public LocalDate getTargetDate() { return targetDate; }
            public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
        }

        public static class Intervention {
            private String interventionId;
            private String type;
            private String description;
            private String frequency;
            private String duration;
            private String instructions;
            private String status;
            public String getInterventionId() { return interventionId; }
            public void setInterventionId(String interventionId) { this.interventionId = interventionId; }
            public String getType() { return type; }
            public void setType(String type) { this.type = type; }
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
            public String getFrequency() { return frequency; }
            public void setFrequency(String frequency) { this.frequency = frequency; }
            public String getDuration() { return duration; }
            public void setDuration(String duration) { this.duration = duration; }
            public String getInstructions() { return instructions; }
            public void setInstructions(String instructions) { this.instructions = instructions; }
            public String getStatus() { return status; }
            public void setStatus(String status) { this.status = status; }
        }

        public static class MonitoringTask {
            private String taskId;
            private String metric;
            private String frequency;
            private String targetRange;
            private String status;
            public String getTaskId() { return taskId; }
            public void setTaskId(String taskId) { this.taskId = taskId; }
            public String getMetric() { return metric; }
            public void setMetric(String metric) { this.metric = metric; }
            public String getFrequency() { return frequency; }
            public void setFrequency(String frequency) { this.frequency = frequency; }
            public String getTargetRange() { return targetRange; }
            public void setTargetRange(String targetRange) { this.targetRange = targetRange; }
            public String getStatus() { return status; }
            public void setStatus(String status) { this.status = status; }
        }

        public static class ExpectedOutcome {
            private String metric;
            private Double baselineValue;
            private Double expectedValue;
            private String unit;
            private String measurementPeriod;
            public String getMetric() { return metric; }
            public void setMetric(String metric) { this.metric = metric; }
            public Double getBaselineValue() { return baselineValue; }
            public void setBaselineValue(Double baselineValue) { this.baselineValue = baselineValue; }
            public Double getExpectedValue() { return expectedValue; }
            public void setExpectedValue(Double expectedValue) { this.expectedValue = expectedValue; }
            public String getUnit() { return unit; }
            public void setUnit(String unit) { this.unit = unit; }
            public String getMeasurementPeriod() { return measurementPeriod; }
            public void setMeasurementPeriod(String measurementPeriod) { this.measurementPeriod = measurementPeriod; }
        }
    }