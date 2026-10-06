package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "adherence_records")
@CompoundIndex(name = "adherence_patient_plan_date_idx", def = "{ 'patientId': 1, 'carePlanId': 1, 'date': -1 }")
public class AdherenceRecord {
    @Id private String id;
    private String adherenceId;
    private String patientId;
    private String carePlanId;
    private String interventionId;
    private String taskId;
    private LocalDate date;
    private String status;
    private LocalDateTime completedAt;
    private String notes;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAdherenceId() { return adherenceId; }
    public void setAdherenceId(String adherenceId) { this.adherenceId = adherenceId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getCarePlanId() { return carePlanId; }
    public void setCarePlanId(String carePlanId) { this.carePlanId = carePlanId; }
    public String getInterventionId() { return interventionId; }
    public void setInterventionId(String interventionId) { this.interventionId = interventionId; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}