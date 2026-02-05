package com.example.backend;


import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Backend Application Context Tests")
@TestPropertySource(locations = "classpath:application-test.properties")
class BackendApplicationTests {

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // If this test runs without throwing an exception, 
        // the application context loaded successfully
    }
}