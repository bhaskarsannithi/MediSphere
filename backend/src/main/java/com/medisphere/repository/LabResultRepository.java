package com.medisphere.repository;

import com.medisphere.model.LabResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabResultRepository extends MongoRepository<LabResult, String> {
    List<LabResult> findByPatientId(String patientId);
}
