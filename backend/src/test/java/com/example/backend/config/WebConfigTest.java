package com.example.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WebConfigTest {

    @Mock
    private CorsRegistry corsRegistry;

    private WebConfig webConfig;

    @BeforeEach
    public void setup() {
        webConfig = new WebConfig();
    }

    @DisplayName("Test addCorsMappings method")
    @Test
    public void testAddCorsMappings() {
        when(corsRegistry.addMapping("/api/**"))
                .thenReturn(corsRegistry);

        webConfig.addCorsMappings(corsRegistry);

        verify(corsRegistry).addMapping("/api/**")
                .withCallRealMethod()
                .withArguments("/api/**");
    }

    @DisplayName("Test allowedOrigins method")
    @Test
    public void testAllowedOrigins() {
        webConfig.addCorsMappings(corsRegistry);

        verify(corsRegistry).addMapping("/api/**")
                .allowedOrigins("http://localhost:5174");
    }

    @DisplayName("Test allowedMethods method")
    @Test
    public void testAllowedMethods() {
        webConfig.addCorsMappings(corsRegistry);

        verify(corsRegistry).addMapping("/api/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    @DisplayName("Test allowedHeaders method")
    @Test
    public void testAllowedHeaders() {
        webConfig.addCorsMappings(corsRegistry);

        verify(corsRegistry).addMapping("/api/**")
                .allowedHeaders("*");
    }

    @DisplayName("Test allowCredentials method")
    @Test
    public void testAllowCredentials() {
        webConfig.addCorsMappings(corsRegistry);

        verify(corsRegistry).addMapping("/api/**")
                .allowCredentials(true);
    }
}