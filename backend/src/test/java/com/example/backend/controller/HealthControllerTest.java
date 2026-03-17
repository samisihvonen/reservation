package com.example.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    private HealthController healthController;

    @BeforeEach
    void setUp() {
        healthController = new HealthController();
    }

    @Test
    void health_ShouldReturnOkStatus() {
        // Act
        ResponseEntity<String> response = healthController.health();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    void health_ResponseBodyShouldBeUppercase() {
        // Act
        ResponseEntity<String> response = healthController.health();

        // Assert
        assertEquals("OK", response.getBody());
    }

    @Test
    void health_ShouldNotReturnNull() {
        // Act
        ResponseEntity<String> response = healthController.health();

        // Assert
        assertEquals("OK", response.getBody());
    }
}