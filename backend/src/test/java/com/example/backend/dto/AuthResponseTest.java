package com.example.backend.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        authResponse = new AuthResponse();
    }

    @Test
    @DisplayName("Should get_token successfully")
    void testGetToken() throws Exception {
        // Arrange

        // Act
        String result = authResponse.getToken();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_token successfully")
    void testSetToken() throws Exception {
        // Arrange

        // Act
        authResponse.setToken("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_type successfully")
    void testGetType() throws Exception {
        // Arrange

        // Act
        String result = authResponse.getType();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_type successfully")
    void testSetType() throws Exception {
        // Arrange

        // Act
        authResponse.setType("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_id successfully")
    void testGetId() throws Exception {
        // Arrange

        // Act
        Long result = authResponse.getId();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_id successfully")
    void testSetId() throws Exception {
        // Arrange

        // Act
        authResponse.setId(1L);

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_email successfully")
    void testGetEmail() throws Exception {
        // Arrange

        // Act
        String result = authResponse.getEmail();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_email successfully")
    void testSetEmail() throws Exception {
        // Arrange

        // Act
        authResponse.setEmail("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_display_name successfully")
    void testGetDisplayName() throws Exception {
        // Arrange

        // Act
        String result = authResponse.getDisplayName();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_display_name successfully")
    void testSetDisplayName() throws Exception {
        // Arrange

        // Act
        authResponse.setDisplayName("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }
}
