package com.example.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WebConfigTest {

    @Mock
    private CorsRegistry registry;
    private MockMvc mockMvc;
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = mockMvc(webApplicationContext);
    }

    @Test
    @DisplayName("Testing addCorsMappings method")
    public void testAddCorsMappings() {
        when(registry.addMapping("/api/**"))
                .thenReturn(registry)
                .thenThrow(IllegalArgumentException.class);

        new WebConfig().addCorsMappings(registry);

        verify(registry, times(1)).addMapping("/api/**");
        verify(registry, times(1)).allowedOrigins("http://localhost:5174");
        verify(registry, times(1)).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registry, times(1)).allowedHeaders("*");
        verify(registry, times(1)).allowCredentials(true);
    }
}