package com.example.backend.config;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import static org.mockito.Mockito.*;

class WebConfigTest {

    private WebConfig webConfig;
    private CorsRegistry corsRegistryMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        webConfig = new WebConfig();
        corsRegistryMock = mock(CorsRegistry.class);
    }

    @Test
    @DisplayName("Test addCorsMappings with valid arguments")
    void testAddCorsMappings() {
        when(corsRegistryMock.addMapping("/api/**")).thenReturn(corsRegistryMock);

        webConfig.addCorsMappings(corsRegistryMock);

        verify(corsRegistryMock).addMapping("/api/**");
        verify(corsRegistryMock).allowedOrigins("http://localhost:5174");
        verify(corsRegistryMock).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(corsRegistryMock).allowedHeaders("*");
        verify(corsRegistryMock, times(1)).allowCredentials(true);
    }
}