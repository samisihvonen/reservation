package com.example.backend.service;

import com.example.backend.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
