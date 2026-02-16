package com.example.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SwaggerConfigTest {

    @Mock
    private OpenAPI openAPI;

    @InjectMocks
    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Test customOpenAPI method")
    void testCustomOpenAPI() {
        // Given
        Info info = new Info().title("Kokoushuoneiden Varausjärjestelmä API")
                .description("REST API dokumentaatio varausjärjestelmälle. " +
                        "Käyttäjät voivat rekisteröityä, kirjautua sisään ja varata kokoushuoneita.")
                .version("1.0.0")
                .contact(new Contact().name("Kehitystiimi").url("https://github.com/yourusername/reservation-system"))
                .license(new License().name("MIT License"));

        Components components = new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token"));

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearer-jwt");

        when(openAPI.getComponents()).thenReturn(components);
        when(openAPI.getInfo()).thenReturn(info);

        // When
        OpenAPI result = swaggerConfig.customOpenAPI();

        // Then
        assertEquals(result, openAPI);
        verify(openAPI).getComponents().equals(components);
        verify(openAPI).getInfo().equals(info);
        verify(openAPI).addSecurityItem(securityRequirement);
    }
}