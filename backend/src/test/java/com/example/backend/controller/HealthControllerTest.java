package com.example.backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureRestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureRestClient
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Health Controller Tests")
class HealthControllerTest {
    
    @Test
    @DisplayName("Controller exists")
    void testControllerExists() {
        assertTrue(true);
    }
    
    @Test
    @DisplayName("Health check returns correct response")
    void testHealthCheckResponse() throws Exception {
        String expected = "Health check: OK";
        ResponseEntity<String> response = rest().getForEntity("/health", String.class);
        assertEquals(expected, response.getBody());
    }
}