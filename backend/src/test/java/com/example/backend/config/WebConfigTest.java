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
    private CorsRegistry registry;

    private WebConfig webConfig;

    @BeforeEach
    public void setup() {
        webConfig = new WebConfig();
    }

    @DisplayName("Test addCorsMappings method")
    @Test
    public void testAddCorsMappings() {
        when(registry.addMapping("/api/**"))
                .thenReturn(registry);

        webConfig.addCorsMappings(registry);

        verify(registry).addMapping("/api/**");
        verify(registry).allowedOrigins("http://localhost:5174");
        verify(registry).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registry).allowedHeaders("*");
        verify(registry).allowCredentials(true);
    }
}