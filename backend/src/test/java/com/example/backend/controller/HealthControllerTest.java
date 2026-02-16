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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HealthControllerTest {

    private MockMvc mockMvc;
    private HealthController healthController;
    private Logger log;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new HealthController()).build();
        this.healthController = mockMvc.getMockController().getComponent(HealthController.class);
        this.log = LoggerFactory.getLogger(HealthController.class);
    }

    @Test
    @DisplayName("Given health() method is called, then it should return a ResponseEntity with status OK and the message 'Health check: OK'")
    public void testHealth_shouldReturnOkAndCorrectMessage() {
        when(log.isInfoEnabled()).thenReturn(true);
        doNothing().when(log).info(anyString());

        ResponseEntity<String> response = healthController.health();

        verify(log, times(1)).info(anyString());
        assertEquals(ResponseEntity.ok("Health check: OK"), response);
    }
}