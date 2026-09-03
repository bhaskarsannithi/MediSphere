package com.medisphere.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medisphereOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MediSphere API")
                        .description("FHIR, digital twin, consent and monitoring APIs for the MediSphere Cognitive Twin")
                        .version("1.0.0"));
    }
}
