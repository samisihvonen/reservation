package com.example.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private Logger logger;

    @InjectMocks
    private HealthController healthController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(healthController).build();
    }

    @Test
    @DisplayName("Should return OK status with health check message")
    void health_ShouldReturnOkStatusWithHealthCheckMessage() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Health check: OK"));
    }

    @Test
    @DisplayName("Should return ResponseEntity with OK status and correct message")
    void health_ShouldReturnResponseEntityWithOkStatusAndCorrectMessage() {
        ResponseEntity<String> response = healthController.health();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Health check: OK");
    }

    @Test
    @DisplayName("Should log health check initiation with current date")
    void health_ShouldLogHealthCheckInitiationWithCurrentDate() {
        healthController.health();

        verify(logger).info("Health check initiated at: " + new java.util.Date());
    }

    @Test
    @DisplayName("Should return content type as text/plain")
    void health_ShouldReturnContentTypeAsTextPlain() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    @DisplayName("Should handle GET request method only")
    void health_ShouldHandleGetRequestMethodOnly() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/health"))
                .andExpect(status().isMethodNotAllowed());
    }
}