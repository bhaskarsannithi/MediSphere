package com.medisphere.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "lab_results")
@CompoundIndex(name = "labs_patient_timestamp_idx", def = "{ 'patientId': 1, 'timestamp': -1 }")
public class LabResult {
    @Id
    private String id;
    private String labResultId;
    private String patientId;
    private String testName;
    private String value;
    private String unit;
    private String referenceRange;
    private LocalDateTime timestamp;
    private String source;

    public LabResult() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLabResultId() { return labResultId; }
    public void setLabResultId(String labResultId) { this.labResultId = labResultId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getReferenceRange() { return referenceRange; }
    public void setReferenceRange(String referenceRange) { this.referenceRange = referenceRange; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
