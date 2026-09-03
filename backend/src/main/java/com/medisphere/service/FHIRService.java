package com.medisphere.service;

import com.medisphere.model.FHIRResource;
import com.medisphere.model.Patient;
import com.medisphere.repository.FHIRResourceRepository;
import com.medisphere.repository.PatientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FHIRService {

    private final FHIRResourceRepository fhirResourceRepository;
    private final PatientRepository patientRepository;
    private final DigitalTwinService digitalTwinService;
    private final ObjectMapper objectMapper;

    public FHIRService(FHIRResourceRepository fhirResourceRepository,
                      PatientRepository patientRepository,
                      DigitalTwinService digitalTwinService,
                      ObjectMapper objectMapper) {
        this.fhirResourceRepository = fhirResourceRepository;
        this.patientRepository = patientRepository;
        this.digitalTwinService = digitalTwinService;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> processFHIRResource(Map<String, Object> payload) {
        validate(payload);
        String resourceType = (String) payload.get("resourceType");
        if (resourceType == null || resourceType.isBlank()) {
            throw new IllegalArgumentException("FHIR resource type is required");
        }

        String patientId = "Patient".equals(resourceType)
                ? normalizePatientIdFromReference((String) payload.get("id"))
                : normalizePatientIdFromReference(referenceValue(extractPatientReference(payload)));
        if (patientId == null || patientId.isBlank() || "UNKNOWN".equals(patientId)) {
            throw new IllegalArgumentException(resourceType + " requires a valid patient reference or id.");
        }
        if (!"Patient".equals(resourceType) && !patientRepository.existsByPatientId(patientId)) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }
        FHIRResource resource = new FHIRResource();
        resource.setResourceId(UUID.randomUUID().toString());
        resource.setPatientId(patientId);
        resource.setResourceType(resourceType);
        resource.setFhirResourceId((String) payload.get("id"));
        try {
            resource.setResourceJson(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("FHIR resource must be valid JSON", exception);
        }
        resource.setValidationStatus("VALID");
        resource.setSource("FHIR_API");
        resource.setCreatedAt(LocalDateTime.now());
        fhirResourceRepository.save(resource);

        digitalTwinService.updateTwin(patientId, Map.of(
                "fhirResources", List.of(resource.getResourceId()),
                "currentVitals", Map.of("source", "FHIR")
        ));

        return Map.of(
                "success", true,
                "resourceId", resource.getResourceId(),
                "patientId", patientId,
                "status", "VALID"
        );
    }

    public Patient createPatientFromFHIR(Map<String, Object> payload) {
        Object nameValue = payload.get("name");
        String firstName = "";
        String lastName = "";
        if (nameValue instanceof List<?> names && !names.isEmpty() && names.get(0) instanceof Map<?, ?>) {
            nameValue = names.get(0);
        }
        if (nameValue instanceof Map<?, ?> name) {
            String text = (String) name.get("text");
            if (text != null && !text.isBlank()) {
                String[] parts = text.split(" ", 2);
                firstName = parts[0];
                if (parts.length > 1) lastName = parts[1];
            }
        }

        String patientId = "MS-" + System.currentTimeMillis() % 100000;
        Patient patient = new Patient();
        patient.setPatientId(patientId);
        patient.setFhirPatientId((String) payload.get("id"));
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setGender((String) payload.get("gender"));
        patient.setActive(Boolean.TRUE);
        patient.setConsentStatus("GRANTED");
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());
        Patient saved = patientRepository.save(patient);
        digitalTwinService.updateTwin(saved.getPatientId(), Map.of("demographics", Map.of(
            "firstName", saved.getFirstName(), "lastName", saved.getLastName(), "gender", saved.getGender())));
        return saved;
    }

    public FHIRResource getResourceById(String id) {
        return fhirResourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FHIR resource not found: " + id));
    }

    public List<FHIRResource> getAllResources() {
        return fhirResourceRepository.findAll();
    }

    public List<FHIRResource> getResources(String patientId) {
        return fhirResourceRepository.findByPatientId(patientId);
    }

    public void validate(Map<String, Object> payload) {
        Object resourceType = payload.get("resourceType");
        if (!(resourceType instanceof String type) || !List.of("Patient", "Observation", "DiagnosticReport").contains(type)) {
            throw new IllegalArgumentException("resourceType must be Patient, Observation, or DiagnosticReport");
        }
        if (!"Patient".equals(resourceType) && extractPatientReference(payload) == null) {
            throw new IllegalArgumentException(type + " requires subject.reference");
        }
    }

    public List<Map<String, Object>> getObservations(String patientId) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<FHIRResource> resources = fhirResourceRepository.findByPatientId(patientId);
        for (FHIRResource resource : resources) {
            if ("Observation".equals(resource.getResourceType())) {
                results.add(Map.of(
                        "id", resource.getFhirResourceId(),
                        "resourceType", resource.getResourceType(),
                        "status", resource.getValidationStatus(),
                        "patientId", resource.getPatientId()
                ));
            }
        }
        return results;
    }

    private Map<String, Object> extractPatientReference(Map<String, Object> payload) {
        Object subject = payload.get("subject");
        if (subject instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        Object patient = payload.get("patient");
        if (patient instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    private String referenceValue(Map<String, Object> reference) {
        return reference == null ? null : (String) reference.get("reference");
    }

    private String normalizePatientIdFromReference(String ref) {
        if (ref == null || ref.isBlank()) return "UNKNOWN";
        String[] parts = ref.split("/");
        return parts.length > 1 ? parts[1] : ref;
    }
}
