package com.example.backend.model;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should get_id successfully")
    void testGetId() throws Exception {
        // Arrange

        // Act
        Long result = user.getId();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_id successfully")
    void testSetId() throws Exception {
        // Arrange

        // Act
        user.setId(1L);

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_email successfully")
    void testGetEmail() throws Exception {
        // Arrange

        // Act
        String result = user.getEmail();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_email successfully")
    void testSetEmail() throws Exception {
        // Arrange

        // Act
        user.setEmail("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_password successfully")
    void testGetPassword() throws Exception {
        // Arrange

        // Act
        String result = user.getPassword();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_password successfully")
    void testSetPassword() throws Exception {
        // Arrange

        // Act
        user.setPassword("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_display_name successfully")
    void testGetDisplayName() throws Exception {
        // Arrange

        // Act
        String result = user.getDisplayName();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_display_name successfully")
    void testSetDisplayName() throws Exception {
        // Arrange

        // Act
        user.setDisplayName("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should is_enabled successfully")
    void testIsEnabled() throws Exception {
        // Arrange

        // Act
        boolean result = user.isEnabled();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_enabled successfully")
    void testSetEnabled() throws Exception {
        // Arrange

        // Act
        user.setEnabled(true);

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_created_at successfully")
    void testGetCreatedAt() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = user.getCreatedAt();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_created_at successfully")
    void testSetCreatedAt() throws Exception {
        // Arrange

        // Act
        user.setCreatedAt(LocalDateTime.now());

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_updated_at successfully")
    void testGetUpdatedAt() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = user.getUpdatedAt();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_updated_at successfully")
    void testSetUpdatedAt() throws Exception {
        // Arrange

        // Act
        user.setUpdatedAt(LocalDateTime.now());

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }
}
