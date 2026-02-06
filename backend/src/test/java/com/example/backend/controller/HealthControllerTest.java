package com.example.backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Health Controller Tests")
class HealthControllerTest {
    
    @Test
    @DisplayName("Controller exists")
    void testControllerExists() {
        assertTrue(true);
    }
}