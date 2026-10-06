package com.medisphere.service;

import com.medisphere.model.Vital;
import com.medisphere.repository.PatientRepository;
import com.medisphere.repository.VitalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VitalServiceValidationTest {
    @Mock VitalRepository vitalRepository;
    @Mock PatientRepository patientRepository;
    @Mock DigitalTwinService twinService;
    @Mock AuditService auditService;
    @Mock AlertService alertService;
    @InjectMocks VitalService vitalService;

    @Test
    void rejectsInvalidHeartRate() {
        Vital vital = vital("HEART_RATE", 300.0, "bpm");
        assertThrows(IllegalArgumentException.class, () -> vitalService.ingest(vital, "test"));
    }

    @Test
    void rejectsInvalidBloodPressure() {
        Vital vital = vital("BLOOD_PRESSURE_SYSTOLIC", 20.0, "mmHg");
        assertThrows(IllegalArgumentException.class, () -> vitalService.ingest(vital, "test"));
    }

    @Test
    void rejectsInvalidOxygenSaturation() {
        Vital vital = vital("SPO2", 120.0, "%");
        assertThrows(IllegalArgumentException.class, () -> vitalService.ingest(vital, "test"));
    }

    @Test
    void rejectsUnknownVitalType() {
        Vital vital = vital("UNKNOWN", 1.0, "unit");
        assertThrows(IllegalArgumentException.class, () -> vitalService.ingest(vital, "test"));
    }

    @Test
    void rejectsMissingRequiredValue() {
        Vital vital = vital("HEART_RATE", null, "bpm");
        assertThrows(IllegalArgumentException.class, () -> vitalService.ingest(vital, "test"));
    }

    private Vital vital(String type, Double value, String unit) {
        Vital vital = new Vital();
        vital.setPatientId("MS-10001");
        vital.setType(type);
        vital.setValue(value);
        vital.setUnit(unit);
        return vital;
    }
}