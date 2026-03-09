package com.example.backend.controller;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.AuthResponse;
import com.example.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "testuser",
                "test@example.com",
                "password123",
                "Test User"
        );

        loginRequest = new LoginRequest(
                "test@example.com",
                "password123"
        );

        authResponse = new AuthResponse(
                "token123",
                "Bearer",
                3600L,
                "USER"
        );
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenRegistrationIsSuccessful() {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void register_ShouldThrowException_WhenEmailIsAlreadyTaken() {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email is already taken"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.register(registerRequest);
        });

        assertEquals("Email is already taken", exception.getMessage());
    }

    @Test
    void register_ShouldThrowException_WhenInputIsInvalid() {
        RegisterRequest invalidRequest = new RegisterRequest(
                "",
                "invalid-email",
                "short",
                ""
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid input data"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.register(invalidRequest);
        });

        assertEquals("Invalid input data", exception.getMessage());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void login_ShouldThrowException_WhenAccountIsLocked() {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Account is locked"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.login(loginRequest);
        });

        assertEquals("Account is locked", exception.getMessage());
    }
}