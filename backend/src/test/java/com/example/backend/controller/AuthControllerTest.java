package com.example.backend.controller;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.AuthResponse;
import com.example.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    private static class StubAuthService extends AuthService {
        private AuthResponse registerResponse;
        private AuthResponse loginResponse;
        private RuntimeException registerException;
        private RuntimeException loginException;

        StubAuthService() {
            super(null, null, null);
        }

        @Override
        public AuthResponse register(RegisterRequest request) {
            if (registerException != null) {
                throw registerException;
            }
            return registerResponse;
        }

        @Override
        public AuthResponse login(LoginRequest request) {
            if (loginException != null) {
                throw loginException;
            }
            return loginResponse;
        }
    }

    private StubAuthService authService;
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        authService = new StubAuthService();
        authController = new AuthController(authService);

        registerRequest = new RegisterRequest(
                "test@example.com",
                "Test User",
                "password123");

        loginRequest = new LoginRequest(
                "test@example.com",
                "password123");

        authResponse = new AuthResponse(
                "token123",
                1L,
                "test@example.com",
                "Test User");

        authService.registerResponse = authResponse;
        authService.loginResponse = authResponse;
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenRegistrationIsSuccessful() {
        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void register_ShouldThrowException_WhenEmailIsAlreadyTaken() {
        authService.registerException = new RuntimeException("Email is already taken");

        Exception exception = assertThrows(RuntimeException.class,
                () -> authController.register(registerRequest));

        assertEquals("Email is already taken", exception.getMessage());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        authService.loginException = new RuntimeException("Invalid email or password");

        Exception exception = assertThrows(RuntimeException.class,
                () -> authController.login(loginRequest));

        assertEquals("Invalid email or password", exception.getMessage());
    }
}