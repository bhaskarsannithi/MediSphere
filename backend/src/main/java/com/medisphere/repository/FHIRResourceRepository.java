package com.medisphere.repository;

import com.medisphere.model.FHIRResource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FHIRResourceRepository extends MongoRepository<FHIRResource, String> {
    List<FHIRResource> findByPatientId(String patientId);
}
