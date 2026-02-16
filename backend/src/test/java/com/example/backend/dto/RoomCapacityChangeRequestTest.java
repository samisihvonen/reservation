package com.example.backend.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomCapacityChangeRequestTest {

    private RoomCapacityChangeRequest roomCapacityChangeRequest;

    @BeforeEach
    void setUp() {
        roomCapacityChangeRequest = new RoomCapacityChangeRequest();
    }

    @Test
    @DisplayName("Should get_new_capacity successfully")
    void testGetNewCapacity() throws Exception {
        // Arrange

        // Act
        Integer result = roomCapacityChangeRequest.getNewCapacity();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_new_capacity successfully")
    void testSetNewCapacity() throws Exception {
        // Arrange

        // Act
        roomCapacityChangeRequest.setNewCapacity(1);

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }
}
