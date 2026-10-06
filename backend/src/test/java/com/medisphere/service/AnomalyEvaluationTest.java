package com.medisphere.service;

import com.medisphere.model.Alert;
import com.medisphere.model.Vital;
import com.medisphere.repository.AlertRepository;
import com.medisphere.repository.VitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalyEvaluationTest {
    @Mock AlertRepository alertRepository;
    @Mock VitalRepository vitalRepository;
    @Mock AuditService auditService;
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertService = new AlertService(alertRepository, vitalRepository, auditService, 15);
        when(alertRepository.findTopByPatientIdAndVitalTypeAndAlertTypeAndStatusInAndCreatedAtAfterOrderByCreatedAtDesc(
                any(), any(), any(), any(), any())).thenReturn(Optional.empty());
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void measuresPrecisionOnSyntheticLabeledHeartRateReadings() {
        List<Double> values = List.of(62.0, 68.0, 72.0, 76.0, 145.0, 32.0, 80.0, 90.0);
        List<Boolean> labels = List.of(false, false, false, false, true, true, false, false);
        List<Boolean> predictions = new ArrayList<>();

        for (int index = 0; index < values.size(); index++) {
            Vital vital = vital("synthetic-" + index, values.get(index));
            when(vitalRepository.findByPatientId("SYNTHETIC-1")).thenReturn(List.of());
            alertService.evaluate(vital, LocalDateTime.now());
            predictions.add(values.get(index) >= 140 || values.get(index) <= 35);
        }

        long truePositives = 0;
        long falsePositives = 0;
        for (int index = 0; index < labels.size(); index++) {
            if (predictions.get(index) && labels.get(index)) truePositives++;
            if (predictions.get(index) && !labels.get(index)) falsePositives++;
        }
        double precision = (double) truePositives / (truePositives + falsePositives);

        assertTrue(precision > 0.85, "Measured synthetic precision was " + precision);
    }

    private Vital vital(String vitalId, double value) {
        Vital vital = new Vital();
        vital.setVitalId(vitalId);
        vital.setPatientId("SYNTHETIC-1");
        vital.setType("HEART_RATE");
        vital.setValue(value);
        vital.setUnit("bpm");
        vital.setTimestamp(LocalDateTime.now());
        vital.setSource("SYNTHETIC_EVALUATION");
        return vital;
    }
}