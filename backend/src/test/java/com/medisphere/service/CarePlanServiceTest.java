package com.medisphere.service;

import com.medisphere.model.AdherenceRecord;
import com.medisphere.model.CarePlan;
import com.medisphere.model.Outcome;
import com.medisphere.repository.AdherenceRepository;
import com.medisphere.repository.AlertRepository;
import com.medisphere.repository.CarePlanRepository;
import com.medisphere.repository.OutcomeRepository;
import com.medisphere.repository.RiskPredictionRepository;
import com.medisphere.repository.VitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarePlanServiceTest {
    @Mock CarePlanRepository carePlanRepository;
    @Mock AdherenceRepository adherenceRepository;
    @Mock OutcomeRepository outcomeRepository;
    @Mock PatientService patientService;
    @Mock DigitalTwinService twinService;
    @Mock RiskPredictionRepository predictionRepository;
    @Mock VitalRepository vitalRepository;
    @Mock AlertRepository alertRepository;
    @Mock AuditService auditService;
    @Mock CarePlanEventPublisher eventPublisher;
    private CarePlanService service;

    @BeforeEach
    void setUp() {
        service = new CarePlanService(carePlanRepository, adherenceRepository, outcomeRepository, patientService,
                twinService, predictionRepository, vitalRepository, alertRepository,
                new ClinicalGuidelineService(), auditService, eventPublisher);
    }

    @Test
    void calculatesAdherenceForCompletedAndMissedTasks() {
        AdherenceRecord completed = record("COMPLETED");
        AdherenceRecord missed = record("MISSED");
        AdherenceRecord pending = record("PENDING");
        when(adherenceRepository.findByCarePlanIdOrderByDateDesc("plan-1")).thenReturn(List.of(completed, missed, pending));

        Map<String, Object> result = service.adherenceStats("plan-1");

        assertEquals(3L, result.get("totalTasks"));
        assertEquals(1L, result.get("completedTasks"));
        assertEquals(33.333333333333336, result.get("adherencePercentage"));
    }

    @Test
    void rejectsDuplicateAdherenceRecord() {
        AdherenceRecord record = record("COMPLETED");
        when(adherenceRepository.existsByCarePlanIdAndTaskIdAndDate(any(), any(), any())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.recordAdherence(record));
    }

    @Test
    void calculatesOutcomeProgressAndUpdatesTwin() {
        Outcome outcome = new Outcome();
        outcome.setPatientId("P001"); outcome.setCarePlanId("plan-1"); outcome.setMetric("BP");
        outcome.setBaselineValue(160.0); outcome.setCurrentValue(145.0); outcome.setTargetValue(130.0); outcome.setUnit("mmHg");
        when(outcomeRepository.save(any(Outcome.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Outcome saved = service.saveOutcome(outcome);

        assertEquals(-15.0, saved.getChange());
        assertEquals(50.0, saved.getProgressPercentage());
        assertEquals("IN_PROGRESS", saved.getStatus());
    }

    private AdherenceRecord record(String status) {
        AdherenceRecord record = new AdherenceRecord();
        record.setPatientId("P001"); record.setCarePlanId("plan-1"); record.setTaskId("task-1");
        record.setDate(java.time.LocalDate.now()); record.setStatus(status);
        return record;
    }
}