package com.example.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

class WebConfigTest {

    private WebConfig webConfig;

    @BeforeEach
    void setUp() {
        webConfig = new WebConfig();
    }

    @Test
    @DisplayName("Should add_cors_mappings successfully")
    void testAddCorsMappings() {
        // Arrange
        CorsRegistry registry = new CorsRegistry();
        registry.addMapping("/**");

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }
}
