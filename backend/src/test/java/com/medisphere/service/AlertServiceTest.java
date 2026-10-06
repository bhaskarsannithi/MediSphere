package com.medisphere.service;

import com.medisphere.model.Alert;
import com.medisphere.model.Vital;
import com.medisphere.repository.AlertRepository;
import com.medisphere.repository.VitalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    @Mock AlertRepository alertRepository;
    @Mock VitalRepository vitalRepository;
    @Mock AuditService auditService;
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertService = new AlertService(alertRepository, vitalRepository, auditService, 15);
    }

    @Test
    void createsCriticalDoctorAlertForExtremeHeartRate() {
        Vital vital = vital("vital-1", 145);
        when(vitalRepository.findByPatientId("MS-10001")).thenReturn(List.of());
        when(alertRepository.findTopByPatientIdAndVitalTypeAndAlertTypeAndStatusInAndCreatedAtAfterOrderByCreatedAtDesc(
                any(), any(), any(), any(), any())).thenReturn(Optional.empty());
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertService.evaluate(vital, LocalDateTime.now());

        verify(alertRepository).save(argThat(alert -> "CRITICAL".equals(alert.getSeverity())
                && "ROLE_DOCTOR".equals(alert.getAssignedTo()) && "NEW".equals(alert.getStatus())));
        verify(auditService).record(eq("system"), eq("SYSTEM"), eq("ALERT_CREATED"), eq("Alert"), anyString(), eq("MS-10001"), eq("SUCCESS"));
    }

    @Test
    void incrementsExistingAlertInsteadOfCreatingDuplicate() {
        Vital vital = vital("vital-2", 145);
        Alert existing = new Alert();
        existing.setSuppressedCount(2);
        when(vitalRepository.findByPatientId("MS-10001")).thenReturn(List.of());
        when(alertRepository.findTopByPatientIdAndVitalTypeAndAlertTypeAndStatusInAndCreatedAtAfterOrderByCreatedAtDesc(
                any(), any(), any(), any(), any())).thenReturn(Optional.of(existing));

        alertService.evaluate(vital, LocalDateTime.now());

        assertEquals(3, existing.getSuppressedCount());
        verify(alertRepository).save(existing);
        verify(alertRepository, never()).save(argThat(alert -> alert != existing));
    }

    @Test
    void storesResolutionActorAndTimestamp() {
        Alert existing = new Alert();
        existing.setAlertId("alert-1");
        existing.setPatientId("MS-10001");
        existing.setStatus("ACKNOWLEDGED");
        when(alertRepository.findByAlertId("alert-1")).thenReturn(Optional.of(existing));
        when(alertRepository.save(existing)).thenReturn(existing);

        Alert resolved = alertService.updateStatus("alert-1", "RESOLVED", "doctor");

        assertEquals("RESOLVED", resolved.getStatus());
        assertEquals("doctor", resolved.getResolvedBy());
        assertNotNull(resolved.getResolvedAt());
        verify(auditService).record("doctor", "CLINICAL", "ALERT_RESOLVED", "Alert", "alert-1", "MS-10001", "SUCCESS");
    }

    private Vital vital(String vitalId, double value) {
        Vital vital = new Vital();
        vital.setVitalId(vitalId);
        vital.setPatientId("MS-10001");
        vital.setType("HEART_RATE");
        vital.setValue(value);
        vital.setUnit("bpm");
        vital.setTimestamp(LocalDateTime.now());
        vital.setSource("TEST");
        return vital;
    }
}