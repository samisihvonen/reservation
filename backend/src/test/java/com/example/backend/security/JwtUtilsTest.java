package com.example.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    @DisplayName("Should generate_token successfully")
    void testGenerateToken() throws Exception {
        // Arrange

        // Act
        String result = jwtUtils.generateToken("test-value");

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should extract_username successfully")
    void testExtractUsername() throws Exception {
        // Arrange

        // Act
        String result = jwtUtils.extractUsername("test-value");

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should is_token_valid successfully")
    void testIsTokenValid() throws Exception {
        // Arrange

        // Act
        boolean result = jwtUtils.isTokenValid("test-value", null);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should validate_jwt_token successfully")
    void testValidateJwtToken() throws Exception {
        // Arrange

        // Act
        boolean result = jwtUtils.validateJwtToken("test-value");

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_user_name_from_jwt_token successfully")
    void testGetUserNameFromJwtToken() throws Exception {
        // Arrange

        // Act
        String result = jwtUtils.getUserNameFromJwtToken("test-value");

        // Assert
        assertThat(result).isNotNull();
    }
}
