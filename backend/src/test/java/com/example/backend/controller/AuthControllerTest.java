package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void register_ValidRequest_ReturnsAuthResponse() {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void register_InvalidRequest_ReturnsBadRequest() {
        RegisterRequest invalidRequest = new RegisterRequest(
                "",
                "invalid-email",
                "short",
                ""
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Validation failed"));

        assertThrows(RuntimeException.class, () -> {
            authController.register(invalidRequest);
        });
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> {
            authController.login(new LoginRequest("wrong@example.com", "wrongpassword"));
        });
    }

    @Test
    void login_EmptyCredentials_ReturnsBadRequest() {
        LoginRequest emptyRequest = new LoginRequest("", "");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Email and password must not be empty"));

        assertThrows(RuntimeException.class, () -> {
            authController.login(emptyRequest);
        });
    }
}