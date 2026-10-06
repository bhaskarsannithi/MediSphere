package com.medisphere.repository;

import com.medisphere.model.Vital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitalRepository extends MongoRepository<Vital, String> {
    List<Vital> findByPatientId(String patientId);
    List<Vital> findTop50ByOrderByTimestampDesc();
}
