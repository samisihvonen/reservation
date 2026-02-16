package com.example.backend.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomNameChangeRequestTest {

    private RoomNameChangeRequest roomNameChangeRequest;

    @BeforeEach
    void setUp() {
        roomNameChangeRequest = new RoomNameChangeRequest();
    }

    @Test
    @DisplayName("Should get_new_name successfully")
    void testGetNewName() throws Exception {
        // Arrange

        // Act
        String result = roomNameChangeRequest.getNewName();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_new_name successfully")
    void testSetNewName() throws Exception {
        // Arrange

        // Act
        roomNameChangeRequest.setNewName("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }
}
