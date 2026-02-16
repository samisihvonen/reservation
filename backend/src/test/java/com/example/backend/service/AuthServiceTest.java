package com.example.backend.service;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Initialize test fixtures
    }

    @Test
    @DisplayName("Should register successfully")
    void testRegister() {
        // Arrange

        // Act
        AuthResponse result = authService.register(null);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should login successfully")
    void testLogin() {
        // Arrange

        // Act
        AuthResponse result = authService.login(null);

        // Assert
        assertThat(result).isNotNull();
    }
}
