package com.example.backend.dto;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateReservationRequestTest {

    private CreateReservationRequest createReservationRequest;

    @BeforeEach
    void setUp() {
        createReservationRequest = new CreateReservationRequest();
    }

    @Test
    @DisplayName("Should get_room_id successfully")
    void testGetRoomId() {
        // Arrange

        // Act
        String result = createReservationRequest.getRoomId();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_start_time successfully")
    void testGetStartTime() {
        // Arrange

        // Act
        LocalDateTime result = createReservationRequest.getStartTime();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_end_time successfully")
    void testGetEndTime() {
        // Arrange

        // Act
        LocalDateTime result = createReservationRequest.getEndTime();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_user successfully")
    void testGetUser() {
        // Arrange

        // Act
        String result = createReservationRequest.getUser();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_room_id successfully")
    void testSetRoomId() {
        // Arrange

        // Act
        createReservationRequest.setRoomId("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_start_time successfully")
    void testSetStartTime() {
        // Arrange

        // Act
        createReservationRequest.setStartTime(LocalDateTime.now());

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_end_time successfully")
    void testSetEndTime() {
        // Arrange

        // Act
        createReservationRequest.setEndTime(LocalDateTime.now());

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_user successfully")
    void testSetUser() {
        // Arrange

        // Act
        createReservationRequest.setUser("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }
}
