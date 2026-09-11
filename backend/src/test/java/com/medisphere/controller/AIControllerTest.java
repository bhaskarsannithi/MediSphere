package com.medisphere.controller;

import com.medisphere.config.SecurityConfig;
import com.medisphere.model.RiskPrediction;
import com.medisphere.service.AIPredictionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AIController.class)
@Import(SecurityConfig.class)
class AIControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private AIPredictionService predictionService;

    @Test
    void rejectsUnauthenticatedPredictionRequest() throws Exception {
        mockMvc.perform(post("/api/ai/predict/cvd/MS-10001"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsPersistedPredictionForAuthenticatedClinician() throws Exception {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setPatientId("MS-10001");
        prediction.setModelType("CVD");
        prediction.setRiskPercentage(18.4);
        prediction.setRiskCategory("MODERATE");
        when(predictionService.predict(anyString(), anyString(), anyString())).thenReturn(prediction);

        mockMvc.perform(post("/api/ai/predict/cvd/MS-10001").with(httpBasic("doctor", "medisphere-demo")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelType").value("CVD"))
                .andExpect(jsonPath("$.riskPercentage").value(18.4));
    }
}
