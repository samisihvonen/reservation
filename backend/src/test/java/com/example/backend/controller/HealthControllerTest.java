package com.example.backend.controller;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HealthControllerTest {

    private HealthController healthController;
    private Logger mockLogger;

    @BeforeEach
    public void setup() {
        mockLogger = mock(Logger.class);
        healthController = new HealthController(mockLogger);
    }

    @DisplayName("Health check should return OK status and log a message")
    @Test
    public void testHealthCheck() {
        when(mockLogger.info(anyString())).thenReturn(this);

        ResponseEntity<String> response = healthController.health();

        verify(mockLogger, times(1)).info(containsString("Health check initiated at:"));
        assertEquals(ResponseEntity.ok("Health check: OK"), response);
    }
}