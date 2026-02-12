package com.example.backend.controller;

import com.example.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should handle register request")
    void testRegister() throws Exception {
        // Arrange
        // TODO: Mock service calls using authService

        // Act
        ResultActions result = mockMvc.perform(get("/register")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle login request")
    void testLogin() throws Exception {
        // Arrange
        // TODO: Mock service calls using authService

        // Act
        ResultActions result = mockMvc.perform(get("/login")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
    }
}
