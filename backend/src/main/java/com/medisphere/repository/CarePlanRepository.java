package com.medisphere.repository;

import com.medisphere.model.CarePlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CarePlanRepository extends MongoRepository<CarePlan, String> {
    Optional<CarePlan> findByCarePlanId(String carePlanId);
    List<CarePlan> findByPatientIdOrderByCreatedAtDesc(String patientId);
    long countByStatus(String status);
}