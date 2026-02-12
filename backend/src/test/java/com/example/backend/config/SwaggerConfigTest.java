package com.example.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SwaggerConfigTest {

    private SwaggerConfig instance;

    @BeforeEach
    void setUp() {
        instance = new SwaggerConfig();
    }

    @Test
    @DisplayName("Should custom_open_a_p_i successfully")
    void testCustomOpenAPI() {
        // Arrange

        // Act
        OpenAPI result = swaggerConfig.customOpenAPI();

        // Assert
        assertThat(result).isNotNull();
    }
}
