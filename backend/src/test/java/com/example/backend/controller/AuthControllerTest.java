package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest(); // Set up test data for RegisterRequest
        loginRequest = new LoginRequest(); // Set up test data for LoginRequest
        authResponse = new AuthResponse(); // Set up test data for AuthResponse
    }

    @DisplayName("Test register method")
    @Test
    void testRegister() {
        when(authService.register(registerRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService).register(registerRequest);
        verifyNoMoreInteractions(authService);
    }

    @DisplayName("Test login method")
    @Test
    void testLogin() {
        when(authService.login(loginRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).login(loginRequest);
        verifyNoMoreInteractions(authService);
    }
}