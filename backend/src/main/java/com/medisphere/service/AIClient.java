package com.medisphere.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AIClient {
    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public AIClient(@Value("${medisphere.ai.service-url}") String serviceUrl,
                    @Value("${medisphere.ai.connect-timeout-ms}") int connectTimeout,
                    @Value("${medisphere.ai.read-timeout-ms}") int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        this.restTemplate = new RestTemplate(factory);
        this.serviceUrl = serviceUrl;
    }

    public Map<String, Object> predict(String modelType, String patientId, Map<String, Double> features) {
        try {
            return restTemplate.postForObject(serviceUrl + "/predict/" + modelType.toLowerCase(),
                    Map.of("patientId", patientId, "features", features), Map.class);
        } catch (RuntimeException exception) {
            throw new AIServiceUnavailableException("AI service is unavailable or could not process this prediction", exception);
        }
    }

    public Object models() {
        try { return restTemplate.getForObject(serviceUrl + "/models", Object.class); }
        catch (RuntimeException exception) { throw new AIServiceUnavailableException("AI service is unavailable", exception); }
    }

    public Map<String, Object> federatedStatus() {
        try { return restTemplate.getForObject(serviceUrl + "/federated/status", Map.class); }
        catch (RuntimeException exception) { throw new AIServiceUnavailableException("AI service is unavailable", exception); }
    }
}
