package com.medisphere.repository;

import com.medisphere.model.Outcome;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface OutcomeRepository extends MongoRepository<Outcome, String> {
    List<Outcome> findByCarePlanIdOrderByMeasurementDateDesc(String carePlanId);
    List<Outcome> findByPatientIdOrderByMeasurementDateDesc(String patientId);
    Optional<Outcome> findByOutcomeId(String outcomeId);
}