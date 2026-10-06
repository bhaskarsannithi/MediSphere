package com.medisphere.service;

import com.medisphere.model.AdherenceRecord;
import com.medisphere.model.CarePlan;
import com.medisphere.model.HealthTwin;
import com.medisphere.model.Outcome;
import com.medisphere.model.RiskPrediction;
import com.medisphere.model.Vital;
import com.medisphere.repository.AdherenceRepository;
import com.medisphere.repository.AlertRepository;
import com.medisphere.repository.CarePlanRepository;
import com.medisphere.repository.OutcomeRepository;
import com.medisphere.repository.RiskPredictionRepository;
import com.medisphere.repository.VitalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CarePlanService {
    private final CarePlanRepository carePlanRepository;
    private final AdherenceRepository adherenceRepository;
    private final OutcomeRepository outcomeRepository;
    private final PatientService patientService;
    private final DigitalTwinService twinService;
    private final RiskPredictionRepository predictionRepository;
    private final VitalRepository vitalRepository;
    private final AlertRepository alertRepository;
    private final ClinicalGuidelineService guidelineService;
    private final AuditService auditService;
    private final CarePlanEventPublisher eventPublisher;

    public CarePlanService(CarePlanRepository carePlanRepository, AdherenceRepository adherenceRepository,
                           OutcomeRepository outcomeRepository, PatientService patientService,
                           DigitalTwinService twinService, RiskPredictionRepository predictionRepository,
                           VitalRepository vitalRepository, AlertRepository alertRepository,
                           ClinicalGuidelineService guidelineService, AuditService auditService,
                           CarePlanEventPublisher eventPublisher) {
        this.carePlanRepository = carePlanRepository; this.adherenceRepository = adherenceRepository;
        this.outcomeRepository = outcomeRepository; this.patientService = patientService; this.twinService = twinService;
        this.predictionRepository = predictionRepository; this.vitalRepository = vitalRepository;
        this.alertRepository = alertRepository; this.guidelineService = guidelineService; this.auditService = auditService;
        this.eventPublisher = eventPublisher;
    }

    public CarePlan generate(String patientId, String actor) {
        patientService.getPatientById(patientId);
        HealthTwin twin = twinService.getTwinByPatientId(patientId);
        RiskPrediction prediction = predictionRepository.findByPatientIdOrderByPredictionTimestampDesc(patientId).stream().findFirst().orElse(null);
        List<Vital> vitals = vitalRepository.findByPatientId(patientId);
        CarePlan plan = new CarePlan();
        plan.setCarePlanId(UUID.randomUUID().toString()); plan.setPatientId(patientId);
        plan.setTitle("Personalized cardiometabolic care plan");
        plan.setDescription("AI-generated recommendation using the current Digital Twin and recent clinical signals. Requires clinician review.");
        plan.setGeneratedBy(actor); plan.setStatus("PENDING_REVIEW"); plan.setCreatedAt(LocalDateTime.now()); plan.setUpdatedAt(LocalDateTime.now());
        plan.setTimeline(new ArrayList<>(List.of("GENERATED: " + plan.getCreatedAt())));
        Map<String, Object> risk = new HashMap<>();
        if (prediction != null) { risk.put("modelType", prediction.getModelType()); risk.put("riskPercentage", prediction.getRiskPercentage()); risk.put("riskCategory", prediction.getRiskCategory()); risk.put("explanation", prediction.getExplanation()); }
        else if (twin.getRiskPredictions() != null) risk.putAll(twin.getRiskPredictions());
        risk.put("recentAlertCount", alertRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream().limit(5).count());
        plan.setRiskInformation(risk);
        CarePlan.Goal goal = new CarePlan.Goal(); goal.setDescription("Improve monitored cardiometabolic markers"); goal.setTargetMetric("BLOOD_PRESSURE_SYSTOLIC"); goal.setTargetValue(130.0); goal.setTargetUnit("mmHg"); goal.setTargetDate(LocalDate.now().plusDays(90)); plan.setGoals(List.of(goal));
        CarePlan.Intervention lifestyle = new CarePlan.Intervention(); lifestyle.setInterventionId(UUID.randomUUID().toString()); lifestyle.setType("LIFESTYLE"); lifestyle.setDescription("Clinician-reviewed lifestyle intervention"); lifestyle.setFrequency("Daily"); lifestyle.setDuration("90 days"); lifestyle.setInstructions("Review activity, nutrition, and recovery plan with a qualified provider."); lifestyle.setStatus("PROPOSED");
        plan.setInterventions(new ArrayList<>(List.of(lifestyle)));
        CarePlan.MonitoringTask bp = new CarePlan.MonitoringTask(); bp.setTaskId(UUID.randomUUID().toString()); bp.setMetric("BLOOD_PRESSURE_SYSTOLIC"); bp.setFrequency("Daily"); bp.setTargetRange("90-130 mmHg"); bp.setStatus("PENDING");
        plan.setMonitoringTasks(new ArrayList<>(List.of(bp)));
        CarePlan.ExpectedOutcome outcome = new CarePlan.ExpectedOutcome(); outcome.setMetric("BLOOD_PRESSURE_SYSTOLIC"); outcome.setExpectedValue(130.0); outcome.setUnit("mmHg"); outcome.setMeasurementPeriod("90 days"); outcome.setBaselineValue(vitals.stream().filter(v -> "BLOOD_PRESSURE_SYSTOLIC".equals(v.getType())).map(Vital::getValue).findFirst().orElse(null)); plan.setExpectedOutcomes(List.of(outcome));
        plan.setGuidelineValidation(guidelineService.validate(plan));
        CarePlan saved = carePlanRepository.save(plan);
        auditService.record(actor, "CLINICAL", "CAREPLAN_GENERATED", "CarePlan", saved.getCarePlanId(), patientId, "SUCCESS");
        eventPublisher.publish("careplan.generated", saved.getCarePlanId(), patientId, actor);
        return saved;
    }

    public CarePlan get(String id) { return carePlanRepository.findByCarePlanId(id).orElseThrow(() -> new IllegalArgumentException("Care plan not found: " + id)); }
    public List<CarePlan> byPatient(String patientId) { return carePlanRepository.findByPatientIdOrderByCreatedAtDesc(patientId); }
    public CarePlan update(String id, CarePlan changes, String actor) {
        CarePlan plan = get(id);
        if (changes.getTitle() != null) plan.setTitle(changes.getTitle());
        if (changes.getDescription() != null) plan.setDescription(changes.getDescription());
        if (changes.getGoals() != null && !changes.getGoals().isEmpty()) plan.setGoals(changes.getGoals());
        if (changes.getInterventions() != null && !changes.getInterventions().isEmpty()) plan.setInterventions(changes.getInterventions());
        if (changes.getMonitoringTasks() != null && !changes.getMonitoringTasks().isEmpty()) plan.setMonitoringTasks(changes.getMonitoringTasks());
        plan.setGuidelineValidation(guidelineService.validate(plan));
        plan.setStatus("MODIFIED"); plan.setUpdatedAt(LocalDateTime.now()); plan.getTimeline().add("MODIFIED: " + plan.getUpdatedAt());
        auditService.record(actor, "CLINICAL", "CAREPLAN_UPDATED", "CarePlan", id, plan.getPatientId(), "SUCCESS");
        CarePlan saved = carePlanRepository.save(plan);
        eventPublisher.publish("careplan.modified", id, plan.getPatientId(), actor);
        return saved;
    }
    public CarePlan validate(String id) { CarePlan plan = get(id); plan.setGuidelineValidation(guidelineService.validate(plan)); plan.setUpdatedAt(LocalDateTime.now()); plan.getTimeline().add("VALIDATED: " + plan.getUpdatedAt()); CarePlan saved = carePlanRepository.save(plan); eventPublisher.publish("careplan.validated", id, plan.getPatientId(), "system"); return saved; }
    public CarePlan review(String id, String action, String actor, String comments, List<CarePlan.Intervention> interventions) {
        CarePlan plan = get(id);
        if (!"PENDING_REVIEW".equals(plan.getStatus()) && !"MODIFIED".equals(plan.getStatus())) throw new IllegalArgumentException("Care plan is not pending provider review");
        if ("MODIFY".equals(action)) { if (interventions == null || interventions.isEmpty()) throw new IllegalArgumentException("Modified plan requires interventions"); plan.setInterventions(interventions); plan.setStatus("MODIFIED"); }
        else if ("APPROVE".equals(action)) { if (plan.getGuidelineValidation() == null || !Boolean.TRUE.equals(plan.getGuidelineValidation().get("compliant"))) throw new IllegalArgumentException("Care plan must pass guideline validation before approval"); plan.setStatus("ACTIVE"); }
        else if ("REJECT".equals(action)) plan.setStatus("REJECTED");
        else throw new IllegalArgumentException("Unsupported provider action");
        plan.setReviewedBy(actor); plan.setReviewComments(comments); plan.setReviewedAt(LocalDateTime.now()); plan.setUpdatedAt(plan.getReviewedAt()); plan.getTimeline().add(plan.getStatus() + ": " + plan.getUpdatedAt());
        CarePlan saved = carePlanRepository.save(plan); auditService.record(actor, "CLINICAL", "CAREPLAN_" + action, "CarePlan", id, plan.getPatientId(), "SUCCESS"); eventPublisher.publish("careplan." + action.toLowerCase(), id, plan.getPatientId(), actor); return saved;
    }

    public AdherenceRecord recordAdherence(AdherenceRecord record) {
        patientService.getPatientById(record.getPatientId());
        if (record.getCarePlanId() == null || record.getDate() == null || record.getStatus() == null) throw new IllegalArgumentException("carePlanId, date and status are required");
        if (!List.of("PENDING", "COMPLETED", "MISSED", "SKIPPED").contains(record.getStatus())) throw new IllegalArgumentException("Unsupported adherence status");
        if (adherenceRepository.existsByCarePlanIdAndTaskIdAndDate(record.getCarePlanId(), record.getTaskId(), record.getDate())) throw new IllegalArgumentException("Duplicate adherence record");
        record.setAdherenceId(UUID.randomUUID().toString()); if ("COMPLETED".equals(record.getStatus())) record.setCompletedAt(LocalDateTime.now()); AdherenceRecord saved = adherenceRepository.save(record); eventPublisher.publish("intervention." + record.getStatus().toLowerCase(), record.getCarePlanId(), record.getPatientId(), "patient"); return saved;
    }
    public AdherenceRecord updateAdherence(String adherenceId, AdherenceRecord changes) {
        AdherenceRecord record = adherenceRepository.findByAdherenceId(adherenceId).orElseThrow(() -> new IllegalArgumentException("Adherence record not found: " + adherenceId));
        if (changes.getStatus() == null || !List.of("PENDING", "COMPLETED", "MISSED", "SKIPPED").contains(changes.getStatus())) throw new IllegalArgumentException("Unsupported adherence status");
        record.setStatus(changes.getStatus()); record.setNotes(changes.getNotes());
        record.setCompletedAt("COMPLETED".equals(changes.getStatus()) ? LocalDateTime.now() : null);
        return adherenceRepository.save(record);
    }
    public List<AdherenceRecord> adherence(String carePlanId) { return adherenceRepository.findByCarePlanIdOrderByDateDesc(carePlanId); }
    public List<AdherenceRecord> adherenceByPatient(String patientId) { return adherenceRepository.findByPatientIdOrderByDateDesc(patientId); }
    public Map<String, Object> adherenceStats(String carePlanId) { List<AdherenceRecord> records = adherence(carePlanId); long completed = records.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count(); double rate = records.isEmpty() ? 0 : completed * 100.0 / records.size(); return Map.of("totalTasks", records.size(), "completedTasks", completed, "missedTasks", records.stream().filter(r -> "MISSED".equals(r.getStatus())).count(), "pendingTasks", records.stream().filter(r -> "PENDING".equals(r.getStatus())).count(), "adherencePercentage", rate); }
    public Outcome saveOutcome(Outcome outcome) { patientService.getPatientById(outcome.getPatientId()); if (outcome.getCarePlanId() == null || outcome.getMetric() == null || outcome.getCurrentValue() == null || outcome.getTargetValue() == null) throw new IllegalArgumentException("carePlanId, metric, currentValue and targetValue are required"); outcome.setOutcomeId(outcome.getOutcomeId() == null ? UUID.randomUUID().toString() : outcome.getOutcomeId()); outcome.setMeasurementDate(outcome.getMeasurementDate() == null ? LocalDate.now() : outcome.getMeasurementDate()); if (outcome.getBaselineValue() != null) { outcome.setChange(outcome.getCurrentValue() - outcome.getBaselineValue()); double denominator = Math.abs(outcome.getTargetValue() - outcome.getBaselineValue()); outcome.setProgressPercentage(denominator == 0 ? 100 : Math.max(0, Math.min(100, Math.abs(outcome.getCurrentValue() - outcome.getBaselineValue()) / denominator * 100))); } outcome.setStatus(outcome.getCurrentValue() <= outcome.getTargetValue() ? "ON_TARGET" : "IN_PROGRESS"); Outcome saved = outcomeRepository.save(outcome); twinService.updateTwin(outcome.getPatientId(), Map.of("currentVitals", Map.of(outcome.getMetric(), Map.of("value", outcome.getCurrentValue(), "unit", outcome.getUnit(), "updatedBy", "CARE_PLAN_OUTCOME")))); eventPublisher.publish("outcome.updated", outcome.getCarePlanId(), outcome.getPatientId(), "provider"); return saved; }
    public Outcome updateOutcome(String outcomeId, Outcome changes) {
        Outcome outcome = outcomeRepository.findByOutcomeId(outcomeId).orElseThrow(() -> new IllegalArgumentException("Outcome not found: " + outcomeId));
        if (changes.getCurrentValue() == null || changes.getTargetValue() == null) throw new IllegalArgumentException("currentValue and targetValue are required");
        outcome.setCurrentValue(changes.getCurrentValue()); outcome.setTargetValue(changes.getTargetValue()); outcome.setMeasurementDate(changes.getMeasurementDate() == null ? LocalDate.now() : changes.getMeasurementDate());
        return saveOutcome(outcome);
    }
    public List<Outcome> outcomes(String carePlanId) { return outcomeRepository.findByCarePlanIdOrderByMeasurementDateDesc(carePlanId); }
    public Map<String, Object> statistics() { return Map.of("activeCareplans", carePlanRepository.countByStatus("ACTIVE"), "pendingReviews", carePlanRepository.countByStatus("PENDING_REVIEW"), "trackedOutcomes", outcomeRepository.count()); }
}