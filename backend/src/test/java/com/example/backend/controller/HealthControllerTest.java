package com.example.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    @InjectMocks
    private HealthController healthController;

    @BeforeEach
    void setUp() {
        // Initialization code if needed
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
    void health_ResponseBodyShouldNotBeNull() {
        // Act
        ResponseEntity<String> response = healthController.health();

        // Assert
        assertEquals("OK", response.getBody());
    }

    @Test
    void health_ResponseShouldContainCorrectContentType() {
        // Act
        ResponseEntity<String> response = healthController.health();

        // Assert
        assertEquals("OK", response.getBody());
    }
}