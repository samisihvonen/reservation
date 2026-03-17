package com.example.backend.config;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebConfigTest {

    private final WebConfig webConfig = new WebConfig();

    private static class TestCorsRegistry extends CorsRegistry {
        Map<String, CorsConfiguration> configurations() {
            return super.getCorsConfigurations();
        }
    }

    @Test
    @DisplayName("Should configure CORS mappings with correct settings")
    void testAddCorsMappings() {
        TestCorsRegistry registry = new TestCorsRegistry();

        webConfig.addCorsMappings(registry);

        CorsConfiguration config = registry.configurations().get("/api/**");

        assertNotNull(config);
        assertEquals(List.of("http://localhost:5174"), config.getAllowedOrigins());
        assertEquals(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"), config.getAllowedMethods());
        assertEquals(List.of("*"), config.getAllowedHeaders());
        assertEquals(Boolean.TRUE, config.getAllowCredentials());
    }
}