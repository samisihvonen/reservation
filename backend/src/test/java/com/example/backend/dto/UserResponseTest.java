package com.example.backend.dto;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(1L, "test-value", "test-value", LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get_id successfully")
    void testGetId() throws Exception {
        // Arrange

        // Act
        Long result = userResponse.getId();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_email successfully")
    void testGetEmail() throws Exception {
        // Arrange

        // Act
        String result = userResponse.getEmail();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_display_name successfully")
    void testGetDisplayName() throws Exception {
        // Arrange

        // Act
        String result = userResponse.getDisplayName();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_created_at successfully")
    void testGetCreatedAt() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = userResponse.getCreatedAt();

        // Assert
        assertThat(result).isNotNull();
    }
}
