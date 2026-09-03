package com.medisphere.repository;

import com.medisphere.model.Consent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends MongoRepository<Consent, String> {
    List<Consent> findByPatientId(String patientId);
    Optional<Consent> findByConsentId(String consentId);
    Optional<Consent> findTopByPatientIdOrderByGrantedAtDesc(String patientId);
}
