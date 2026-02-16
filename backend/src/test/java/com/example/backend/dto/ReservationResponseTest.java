package com.example.backend.dto;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReservationResponseTest {

    private ReservationResponse reservationResponse;

    @BeforeEach
    void setUp() {
        reservationResponse = new ReservationResponse();
    }

    @Test
    @DisplayName("Should get_id successfully")
    void testGetId() throws Exception {
        // Arrange

        // Act
        String result = reservationResponse.getId();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_room_id successfully")
    void testGetRoomId() throws Exception {
        // Arrange

        // Act
        String result = reservationResponse.getRoomId();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_start_time successfully")
    void testGetStartTime() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = reservationResponse.getStartTime();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_end_time successfully")
    void testGetEndTime() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = reservationResponse.getEndTime();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_user successfully")
    void testGetUser() throws Exception {
        // Arrange

        // Act
        String result = reservationResponse.getUser();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_created_at successfully")
    void testGetCreatedAt() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = reservationResponse.getCreatedAt();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should get_updated_at successfully")
    void testGetUpdatedAt() throws Exception {
        // Arrange

        // Act
        LocalDateTime result = reservationResponse.getUpdatedAt();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should set_id successfully")
    void testSetId() throws Exception {
        // Arrange

        // Act
        reservationResponse.setId("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_room_id successfully")
    void testSetRoomId() throws Exception {
        // Arrange

        // Act
        reservationResponse.setRoomId("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_start_time successfully")
    void testSetStartTime() throws Exception {
        // Arrange

        // Act
        reservationResponse.setStartTime(LocalDateTime.now());

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_end_time successfully")
    void testSetEndTime() throws Exception {
        // Arrange

        // Act
        reservationResponse.setEndTime(LocalDateTime.now());

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_user successfully")
    void testSetUser() throws Exception {
        // Arrange

        // Act
        reservationResponse.setUser("test-value");

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_created_at successfully")
    void testSetCreatedAt() throws Exception {
        // Arrange

        // Act
        reservationResponse.setCreatedAt(LocalDateTime.now());

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }

    @Test
    @DisplayName("Should set_updated_at successfully")
    void testSetUpdatedAt() throws Exception {
        // Arrange

        // Act
        reservationResponse.setUpdatedAt(LocalDateTime.now());

        // Assert
        // TODO: Verify side effects using Mockito.verify()
    }
}
