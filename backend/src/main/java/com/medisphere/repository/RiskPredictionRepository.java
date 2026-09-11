package com.medisphere.repository;

import com.medisphere.model.RiskPrediction;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface RiskPredictionRepository extends MongoRepository<RiskPrediction, String> {
    List<RiskPrediction> findByPatientIdOrderByPredictionTimestampDesc(String patientId);
    Optional<RiskPrediction> findByPredictionId(String predictionId);
}
