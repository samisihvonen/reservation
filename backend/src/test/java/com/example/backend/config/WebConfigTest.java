package com.example.backend.config;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebConfigTest {

    @Mock
    private CorsRegistry registry;
    @Mock
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Test addCorsMappings method")
    void testAddCorsMappings() {
        when(webApplicationContext.getBean(CorsRegistry.class)).thenReturn(registry);

        WebConfig webConfig = new WebConfig();
        webConfig.setApplicationContext(webApplicationContext);

        webConfig.addCorsMappings(registry);

        verify(registry).addMapping("/api/**");
        verify(registry).allowedOrigins("http://localhost:5174");
        verify(registry).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registry).allowedHeaders("*");
        verify(registry, times(1)).allowCredentials(true);
    }
}