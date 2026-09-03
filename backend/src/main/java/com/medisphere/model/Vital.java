package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "vitals")
@CompoundIndex(name = "vitals_patient_timestamp_idx", def = "{ 'patientId': 1, 'timestamp': -1 }")
public class Vital {
    @Id
    private String id;
    private String vitalId;
    private String patientId;
    private String type;
    private Double value;
    private String unit;
    private LocalDateTime timestamp;
    private String source;
    private String validationStatus;

    public Vital() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVitalId() { return vitalId; }
    public void setVitalId(String vitalId) { this.vitalId = vitalId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
}
