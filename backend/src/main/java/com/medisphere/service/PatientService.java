package com.medisphere.service;

import com.medisphere.model.Patient;
import com.medisphere.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(String patientId) {
        return patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
    }

    public Patient createPatient(Patient patient) {
        if (patient.getCreatedAt() == null) {
            patient.setCreatedAt(LocalDateTime.now());
        }
        patient.setUpdatedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }
}
