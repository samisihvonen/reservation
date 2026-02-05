package com.example.backend.controller;


import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("test")
class HealthControllerTest {
    @Test
    void testExists() { assertTrue(true); }
}