package com.medisphere.service;

import com.medisphere.model.*;
import com.medisphere.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SeedDataService implements ApplicationRunner {

    private final PatientRepository patientRepository;
    private final HealthTwinRepository healthTwinRepository;
    private final FHIRResourceRepository fhirResourceRepository;
    private final VitalRepository vitalRepository;
    private final LabResultRepository labResultRepository;
    private final ConsentRepository consentRepository;

    public SeedDataService(PatientRepository patientRepository,
                          HealthTwinRepository healthTwinRepository,
                          FHIRResourceRepository fhirResourceRepository,
                          VitalRepository vitalRepository,
                          LabResultRepository labResultRepository,
                          ConsentRepository consentRepository) {
        this.patientRepository = patientRepository;
        this.healthTwinRepository = healthTwinRepository;
        this.fhirResourceRepository = fhirResourceRepository;
        this.vitalRepository = vitalRepository;
        this.labResultRepository = labResultRepository;
        this.consentRepository = consentRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            seedDemoData();
        } catch (DataAccessException exception) {
            System.err.println("Demo data seeding skipped because MongoDB is unavailable: "
                    + exception.getMessage());
        }
    }

    private void seedDemoData() {
        if (!patientRepository.findAll().isEmpty()) {
            return;
        }

        Patient patient1 = new Patient();
        patient1.setPatientId("MS-10001");
        patient1.setFhirPatientId("fhir-pat-10001");
        patient1.setFirstName("John");
        patient1.setLastName("Doe");
        patient1.setDateOfBirth(LocalDate.of(1972, 3, 15));
        patient1.setGender("male");
        patient1.setActive(true);
        patient1.setConsentStatus("GRANTED");
        patient1.setCreatedAt(LocalDateTime.now());
        patient1.setUpdatedAt(LocalDateTime.now());
        patient1 = patientRepository.save(patient1);

        HealthTwin twin1 = new HealthTwin();
        twin1.setTwinId("TWIN-MS-10001");
        twin1.setPatientId(patient1.getPatientId());
        twin1.setDemographics(Map.of("name", "John Doe", "age", 52, "gender", "male"));
        twin1.setCurrentVitals(Map.of("heartRate", 78, "spO2", 98, "temperature", 36.7));
        twin1.setRecentLabs(List.of(Map.of("testName", "Hemoglobin", "value", "14.8")));
        twin1.setFhirResources(List.of("obs-1", "diag-1"));
        twin1.setConsentStatus("GRANTED");
        twin1.setCompleteness(100);
        twin1.setConnectedSources(List.of("EHR", "LAB", "WEARABLE"));
        twin1.setLastUpdated(LocalDateTime.now());
        healthTwinRepository.save(twin1);

        FHIRResource resource = new FHIRResource();
        resource.setResourceId("obs-1");
        resource.setPatientId(patient1.getPatientId());
        resource.setResourceType("Observation");
        resource.setFhirResourceId("obs-10001-001");
        resource.setResourceJson("{\"resourceType\":\"Observation\",\"id\":\"obs-10001-001\"}");
        resource.setValidationStatus("VALID");
        resource.setSource("DEMO_FHIR");
        resource.setCreatedAt(LocalDateTime.now());
        fhirResourceRepository.save(resource);

        Vital vital = new Vital();
        vital.setVitalId("vital-1");
        vital.setPatientId(patient1.getPatientId());
        vital.setType("HEART_RATE");
        vital.setValue(78.0);
        vital.setUnit("bpm");
        vital.setTimestamp(LocalDateTime.now());
        vital.setSource("DEMO_WEARABLE");
        vital.setValidationStatus("VALID");
        vitalRepository.save(vital);

        LabResult labResult = new LabResult();
        labResult.setLabResultId("lab-001");
        labResult.setPatientId(patient1.getPatientId());
        labResult.setTestName("Hemoglobin");
        labResult.setValue("14.8");
        labResult.setUnit("g/dL");
        labResult.setReferenceRange("13.5-17.5");
        labResult.setTimestamp(LocalDateTime.now());
        labResult.setSource("DEMO_LAB");
        labResultRepository.save(labResult);

        Consent consent = new Consent();
        consent.setConsentId("consent-1");
        consent.setPatientId(patient1.getPatientId());
        consent.setPurpose("Clinical Data Processing");
        consent.setStatus("GRANTED");
        consent.setGrantedAt(LocalDateTime.now());
        consent.setCreatedBy("provider-1");
        consent.setUpdatedAt(LocalDateTime.now());
        consentRepository.save(consent);

        for (int index = 2; index <= 5; index++) {
            String patientId = "MS-1000" + index;
            Patient demoPatient = new Patient();
            demoPatient.setPatientId(patientId);
            demoPatient.setFhirPatientId("fhir-pat-1000" + index);
            demoPatient.setFirstName("Demo");
            demoPatient.setLastName("Patient " + index);
            demoPatient.setDateOfBirth(LocalDate.of(1980 + index, index, 10));
            demoPatient.setGender(index % 2 == 0 ? "female" : "male");
            demoPatient.setActive(true);
            demoPatient.setConsentStatus("GRANTED");
            demoPatient.setCreatedAt(LocalDateTime.now());
            demoPatient.setUpdatedAt(LocalDateTime.now());
            patientRepository.save(demoPatient);

            HealthTwin demoTwin = new HealthTwin();
            demoTwin.setTwinId("TWIN-" + patientId);
            demoTwin.setPatientId(patientId);
            demoTwin.setDemographics(Map.of("name", "Demo Patient " + index, "gender", demoPatient.getGender()));
            demoTwin.setCurrentVitals(Map.of());
            demoTwin.setRecentLabs(List.of());
            demoTwin.setFhirResources(List.of());
            demoTwin.setConsentStatus("GRANTED");
            demoTwin.setCompleteness(45);
            demoTwin.setConnectedSources(List.of("EHR", "LAB", "WEARABLE"));
            demoTwin.setLastUpdated(LocalDateTime.now());
            healthTwinRepository.save(demoTwin);
        }
    }
}
