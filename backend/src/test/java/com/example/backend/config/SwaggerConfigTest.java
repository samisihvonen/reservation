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
        Info info = new Info();
        Contact contact = new Contact().name("Kehitystiimi").url("https://github.com/yourusername/reservation-system");
        License license = new License().name("MIT License");
        info.title("Kokoushuoneiden Varausjärjestelmä API")
                .description("REST API dokumentaatio varausjärjestelmälle. " +
                        "Käyttäjät voivat rekisteröityä, kirjautua sisään ja varata kokoushuoneita.")
                .version("1.0.0")
                .contact(contact)
                .license(license);
        Components components = new Components();
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token");
        components.addSecuritySchemes("bearer-jwt", securityScheme);
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearer-jwt");

        when(openAPI.components(Mockito.<Components>any())).thenReturn(components);
        when(openAPI.info(Mockito.<Info>any())).thenReturn(info);

        // When
        OpenAPI result = swaggerConfig.customOpenAPI();

        // Then
        assertEquals(result, openAPI);
        verify(openAPI).components(components);
        verify(openAPI).info(info);
    }
}