package com.medisphere.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medisphere.model.Vital;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VitalStreamService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final VitalService vitalService;
    private final String topic;

    public VitalStreamService(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper,
                              VitalService vitalService,
                              @Value("${medisphere.kafka.vital-topic:vital-signs}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.vitalService = vitalService;
        this.topic = topic;
    }

    public void publish(Map<String, Object> event) {
        kafkaTemplate.send(topic, String.valueOf(event.get("patientId")), event);
    }

    @KafkaListener(topics = "${medisphere.kafka.vital-topic:vital-signs}")
    public void consume(Map<String, Object> event) {
        Vital vital = objectMapper.convertValue(event, Vital.class);
        vital.setSource(vital.getSource() == null ? "KAFKA_WEARABLE" : vital.getSource());
        vitalService.ingest(vital, "kafka-consumer");
    }
}