package com.example.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class WebConfigTest {

    private WebConfig instance;

    @BeforeEach
    void setUp() {
        instance = new WebConfig();
    }

    @Test
    @DisplayName("Should add_cors_mappings successfully")
    void testAddCorsMappings() {
        CorsRegistry registry = new CorsRegistry();
        assertDoesNotThrow(() -> instance.addCorsMappings(registry));
    }
}
