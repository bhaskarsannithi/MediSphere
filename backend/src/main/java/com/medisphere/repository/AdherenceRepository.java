package com.medisphere.repository;

import com.medisphere.model.AdherenceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface AdherenceRepository extends MongoRepository<AdherenceRecord, String> {
    List<AdherenceRecord> findByCarePlanIdOrderByDateDesc(String carePlanId);
    List<AdherenceRecord> findByPatientIdOrderByDateDesc(String patientId);
    Optional<AdherenceRecord> findByAdherenceId(String adherenceId);
    boolean existsByCarePlanIdAndTaskIdAndDate(String carePlanId, String taskId, java.time.LocalDate date);
}