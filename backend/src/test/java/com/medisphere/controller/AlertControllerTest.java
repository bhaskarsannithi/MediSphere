package com.medisphere.controller;

import com.medisphere.config.SecurityConfig;
import com.medisphere.repository.VitalRepository;
import com.medisphere.service.AlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
@Import(SecurityConfig.class)
class AlertControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    @MockBean
    private VitalRepository vitalRepository;

    @Test
    void nurseCannotResolveAlert() throws Exception {
        mockMvc.perform(post("/api/alerts/alert-1/RESOLVED")
                        .with(httpBasic("nurse", "medisphere-demo")))
                .andExpect(status().isForbidden());
    }

    @Test
    void nurseCanReachAcknowledgementEndpoint() throws Exception {
        mockMvc.perform(post("/api/alerts/alert-1/acknowledge")
                        .with(httpBasic("nurse", "medisphere-demo")))
                .andExpect(status().isOk());
    }
}