package com.example.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class HealthControllerTest {

    @Mock
    private Logger log;

    private HealthController healthController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        healthController = new HealthController();
    }

    @DisplayName("Test health method")
    @Test
    void testHealth() {
        String expectedLogMessage = "Health check initiated at: [expected date]";
        when(log.info(expectedLogMessage)).thenReturn(true);

        ResponseEntity<String> responseEntity = healthController.health();
        assertEquals("Health check: OK", responseEntity.getBody());
    }
}