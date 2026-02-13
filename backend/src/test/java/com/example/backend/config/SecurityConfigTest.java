package com.example.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    @DisplayName("Should password_encoder successfully")
    void testPasswordEncoder() {
        // Arrange

        // Act
        PasswordEncoder result = securityConfig.passwordEncoder();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should security_filter_chain successfully")
    void testSecurityFilterChain() throws Exception {
        // Arrange

        // Act
        SecurityFilterChain result = securityConfig.securityFilterChain(null);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should cors_configuration_source successfully")
    void testCorsConfigurationSource() {
        // Arrange

        // Act
        CorsConfigurationSource result = securityConfig.corsConfigurationSource();

        // Assert
        assertThat(result).isNotNull();
    }
}
