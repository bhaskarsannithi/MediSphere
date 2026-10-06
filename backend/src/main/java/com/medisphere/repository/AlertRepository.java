package com.medisphere.repository;

import com.medisphere.model.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends MongoRepository<Alert, String> {
    Optional<Alert> findByAlertId(String alertId);
    List<Alert> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
    List<Alert> findByPatientIdOrderByCreatedAtDesc(String patientId);
    Optional<Alert> findTopByPatientIdAndVitalTypeAndAlertTypeAndStatusInAndCreatedAtAfterOrderByCreatedAtDesc(
            String patientId, String vitalType, String alertType, List<String> statuses, LocalDateTime createdAt);
}