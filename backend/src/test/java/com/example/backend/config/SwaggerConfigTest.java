package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
    @DisplayName("Should configure OpenAPI metadata and security schemes correctly")
    void testCustomOpenAPI() {
        // Act
        OpenAPI result = instance.customOpenAPI();

        // Assert
        assertThat(result).isNotNull();
        
        // Verify Info metadata
        assertThat(result.getInfo().getTitle()).isEqualTo("Kokoushuoneiden Varausjärjestelmä API");
        assertThat(result.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(result.getInfo().getContact().getName()).isEqualTo("Kehitystiimi");

        // Verify Security Scheme
        SecurityScheme bearerScheme = result.getComponents().getSecuritySchemes().get("bearer-jwt");
        assertThat(bearerScheme).isNotNull();
        assertThat(bearerScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(bearerScheme.getScheme()).isEqualTo("bearer");
        assertThat(bearerScheme.getBearerFormat()).isEqualTo("JWT");
        
        // Verify Security Requirement is added to the list
        assertThat(result.getSecurity()).isNotEmpty();
        assertThat(result.getSecurity().get(0).containsKey("bearer-jwt")).isTrue();
    }
}