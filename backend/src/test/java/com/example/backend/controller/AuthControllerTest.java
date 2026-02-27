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
                "firstName",
                "lastName"
        );

        loginRequest = new LoginRequest(
                "test@example.com",
                "password123"
        );

        authResponse = new AuthResponse(
                "token123",
                1L,
                "testuser",
                "test@example.com",
                "firstName",
                "lastName"
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
    void register_ShouldThrowException_WhenUsernameIsAlreadyTaken() {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username is already taken"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.register(registerRequest);
        });

        assertEquals("Username is already taken", exception.getMessage());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenLoginIsSuccessful() {
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