package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        registerRequest = new RegisterRequest(); // setup test data for RegisterRequest
        loginRequest = new LoginRequest(); // setup test data for LoginRequest
        authResponse = new AuthResponse(); // setup test data for AuthResponse
    }

    @Test
    @DisplayName("Test register method with valid request")
    void testRegisterWithValidRequest() {
        when(authService.register(registerRequest)).thenReturn(authResponse);
        ResponseEntity<AuthResponse> response = authController.register(registerRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService).register(registerRequest);
    }

    @Test
    @DisplayName("Test register method with invalid request")
    void testRegisterWithInvalidRequest() {
        when(authService.register(registerRequest)).thenThrow(new RuntimeException("Error during registration"));
        Exception exception = assertThrows(RuntimeException.class, () -> authController.register(registerRequest));
        assertEquals("Error during registration", exception.getMessage());
    }

    @Test
    @DisplayName("Test login method with valid request")
    void testLoginWithValidRequest() {
        when(authService.login(loginRequest)).thenReturn(authResponse);
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).login(loginRequest);
    }

    @Test
    @DisplayName("Test login method with invalid request")
    void testLoginWithInvalidRequest() {
        when(authService.login(loginRequest)).thenThrow(new RuntimeException("Error during login"));
        Exception exception = assertThrows(RuntimeException.class, () -> authController.login(loginRequest));
        assertEquals("Error during login", exception.getMessage());
    }
}