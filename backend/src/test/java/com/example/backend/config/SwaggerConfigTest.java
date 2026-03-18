package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    private SwaggerConfig instance;

    @BeforeEach
    void setUp() {
        instance = new SwaggerConfig();
    }

    @Test
    @DisplayName("Should custom_open_a_p_i successfully")
    void testCustomOpenAPI() {
        OpenAPI result = instance.customOpenAPI();
        assertThat(result).isNotNull();
    }
}
