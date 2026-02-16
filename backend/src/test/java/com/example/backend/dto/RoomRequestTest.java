package com.example.backend.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomRequestTest {

    private RoomRequest roomRequest;

    @BeforeEach
    void setUp() {
        roomRequest = new RoomRequest();
    }

    @Test
    @DisplayName("Should get_name successfully")
    void testGetName() throws Exception {
        // Arrange

        // Act
        String result = roomRequest.getName();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_name successfully")
    void testSetName() throws Exception {
        // Arrange

        // Act
        roomRequest.setName("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_capacity successfully")
    void testGetCapacity() throws Exception {
        // Arrange

        // Act
        Integer result = roomRequest.getCapacity();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_capacity successfully")
    void testSetCapacity() throws Exception {
        // Arrange

        // Act
        roomRequest.setCapacity(1);

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_description successfully")
    void testGetDescription() throws Exception {
        // Arrange

        // Act
        String result = roomRequest.getDescription();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_description successfully")
    void testSetDescription() throws Exception {
        // Arrange

        // Act
        roomRequest.setDescription("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should get_location successfully")
    void testGetLocation() throws Exception {
        // Arrange

        // Act
        String result = roomRequest.getLocation();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_location successfully")
    void testSetLocation() throws Exception {
        // Arrange

        // Act
        roomRequest.setLocation("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }
}
