package com.example.backend.dto;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomResponseTest {

    private RoomResponse roomResponse;

    @BeforeEach
    void setUp() {
        roomResponse = new RoomResponse("test-value", "test-value", 1, "test-value", "test-value", true, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get_id successfully")
    void testGetId() throws Exception {
        // Arrange

        // Act
        String result = roomResponse.getId();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_name successfully")
    void testGetName() throws Exception {
        // Arrange

        // Act
        String result = roomResponse.getName();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_capacity successfully")
    void testGetCapacity() throws Exception {
        // Arrange

        // Act
        Integer result = roomResponse.getCapacity();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_description successfully")
    void testGetDescription() throws Exception {
        // Arrange

        // Act
        String result = roomResponse.getDescription();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_location successfully")
    void testGetLocation() throws Exception {
        // Arrange

        // Act
        String result = roomResponse.getLocation();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_is_active successfully")
    void testGetIsActive() throws Exception {
        // Arrange

        // Act
        Boolean result = roomResponse.getIsActive();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_created_at successfully")
    void testGetCreatedAt() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = roomResponse.getCreatedAt();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_updated_at successfully")
    void testGetUpdatedAt() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = roomResponse.getUpdatedAt();

        // Assert
        assertThat(result).isNotNull();
    }
}
