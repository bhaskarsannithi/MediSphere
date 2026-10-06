package com.medisphere.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CarePlanEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public CarePlanEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                  @Value("${medisphere.kafka.careplan-topic:careplan-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(String eventType, String carePlanId, String patientId, String actor) {
        kafkaTemplate.send(topic, patientId, Map.of(
                "eventType", eventType,
                "carePlanId", carePlanId,
                "patientId", patientId,
                "actor", actor,
                "timestamp", LocalDateTime.now().toString()));
    }
}