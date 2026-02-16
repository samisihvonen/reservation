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
    private AuthResponse registerResponse;
    private AuthResponse loginResponse;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest(); // set up test data for RegisterRequest
        loginRequest = new LoginRequest(); // set up test data for LoginRequest
        registerResponse = new AuthResponse(); // set up test data for AuthResponse
        loginResponse = new AuthResponse(); // set up test data for AuthResponse
    }

    @Test
    @DisplayName("Test register method")
    void testRegister() {
        when(authService.register(registerRequest)).thenReturn(registerResponse);

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService).register(registerRequest);
        verifyNoMoreInteractions(authService);
        assertEquals(registerResponse, response.getBody());
    }

    @Test
    @DisplayName("Test login method")
    void testLogin() {
        when(authService.login(loginRequest)).thenReturn(loginResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).login(loginRequest);
        verifyNoMoreInteractions(authService);
        assertEquals(loginResponse, response.getBody());
    }
}