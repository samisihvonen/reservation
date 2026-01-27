// ============================================
// Swagger/OpenAPI Configuration
// ============================================
// File: src/main/java/com/example/backend/config/SwaggerConfig.java

package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .info(new Info()
                        .title("Kokoushuoneiden Varausjärjestelmä API")
                        .description("REST API dokumentaatio varausjärjestelmälle. " +
                                "Käyttäjät voivat rekisteröityä, kirjautua sisään ja varata kokoushuoneita.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Kehitystiimi")
                                .url("https://github.com/yourusername/reservation-system"))
                        .license(new License()
                                .name("MIT License")));
    }
}