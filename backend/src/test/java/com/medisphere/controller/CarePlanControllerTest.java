package com.medisphere.controller;

import com.medisphere.config.SecurityConfig;
import com.medisphere.service.CarePlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarePlanController.class)
@Import(SecurityConfig.class)
class CarePlanControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean CarePlanService service;

    @Test
    void nurseCannotApproveCarePlan() throws Exception {
        mockMvc.perform(post("/api/careplans/plan-1/approve")
                        .with(httpBasic("nurse", "medisphere-demo")))
                .andExpect(status().isForbidden());
    }

    @Test
    void doctorCanReachValidationEndpoint() throws Exception {
        mockMvc.perform(post("/api/careplans/plan-1/validate")
                        .with(httpBasic("doctor", "medisphere-demo")))
                .andExpect(status().isOk());
    }
}