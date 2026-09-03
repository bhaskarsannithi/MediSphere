package com.medisphere.repository;

import com.medisphere.model.HealthTwin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthTwinRepository extends MongoRepository<HealthTwin, String> {
    Optional<HealthTwin> findByPatientId(String patientId);
}
