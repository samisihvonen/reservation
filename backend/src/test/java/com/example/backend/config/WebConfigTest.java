package com.example.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class WebConfigTest {

    @Mock
    private CorsRegistry registry;

    private WebConfig webConfig;

    @BeforeEach
    public void setup() {
        initMocks(this);
        webConfig = new WebConfig();
    }

    @Test
    @DisplayName("Test addCorsMappings with correct mappings")
    public void testAddCorsMappings() {
        webConfig.addCorsMappings(registry);

        verify(registry).addMapping("/api/**");
        verify(registry).allowedOrigins("http://localhost:5174");
        verify(registry).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registry).allowedHeaders("*");
        verify(registry).allowCredentials(true);
    }
}