package com.example.backend.config;

import com.example.backend.security.AuthTokenFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(new AuthTokenFilter());

    @Test
    @DisplayName("Should return BCryptPasswordEncoder instance")
    void testPasswordEncoder() {
        PasswordEncoder result = securityConfig.passwordEncoder();

        assertThat(result).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(result.encode("password")).isNotEqualTo("password");
    }

    @Test
    @DisplayName("Should configure CORS source with correct policy")
    void testCorsConfigurationSource() {
        CorsConfigurationSource result = securityConfig.corsConfigurationSource();
        CorsConfiguration config = result.getCorsConfiguration(new MockHttpServletRequest("GET", "/api/test"));

        assertThat(result).isNotNull();
        assertThat(config.getAllowedOriginPatterns()).contains("http://localhost:*");
        assertThat(config.getAllowedMethods()).contains("GET", "POST", "DELETE", "PATCH");
        assertThat(config.getAllowCredentials()).isTrue();
        assertThat(config.getMaxAge()).isEqualTo(3600L);
    }
}