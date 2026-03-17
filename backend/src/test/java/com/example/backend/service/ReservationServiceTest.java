package com.example.backend.service;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.exception.ReservationException;
import com.example.backend.model.Reservation;
import com.example.backend.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private CreateReservationRequest request;
    private final String reservationId = "res-123";
    private final String roomId = "room-101";

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setRoomId(roomId);
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setUser("Test User");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        request = new CreateReservationRequest(roomId, start, end, "Test User");
    }

    @Test
    @DisplayName("Should get_reservations_by_room successfully")
    void testGetReservationsByRoom() {
        // Arrange
        given(repository.findByRoomId(roomId)).willReturn(Collections.singletonList(reservation));

        // Act
        List<ReservationResponse> result = reservationService.getReservationsByRoom(roomId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoomId()).isEqualTo(roomId);
        verify(repository).findByRoomId(roomId);
    }

    @Test
    @DisplayName("Should get_reservation_by_id successfully")
    void testGetReservationById() {
        // Arrange
        given(repository.findById(reservationId)).willReturn(Optional.of(reservation));

        // Act
        ReservationResponse result = reservationService.getReservationById(reservationId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reservationId);
    }

    @Test
    @DisplayName("Should throw exception when reservation not found by id")
    void testGetReservationById_NotFound() {
        // Arrange
        given(repository.findById("wrong-id")).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReservationException.class, () -> reservationService.getReservationById("wrong-id"));
    }

    @Test
    @DisplayName("Should create_reservation successfully")
    void testCreateReservation() {
        // Arrange
        given(repository.findByRoomId(roomId)).willReturn(Collections.emptyList());
        given(repository.save(any(Reservation.class))).willReturn(reservation);

        // Act
        ReservationResponse result = reservationService.createReservation(request);

        // Assert
        assertThat(result).isNotNull();
        verify(repository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should update_reservation successfully")
    void testUpdateReservation() {
        // Arrange
        given(repository.findById(reservationId)).willReturn(Optional.of(reservation));
        given(repository.findByRoomId(roomId)).willReturn(Collections.singletonList(reservation));
        given(repository.save(any(Reservation.class))).willReturn(reservation);

        // Act
        ReservationResponse result = reservationService.updateReservation(reservationId, request);

        // Assert
        assertThat(result).isNotNull();
        verify(repository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should delete_reservation successfully")
    void testDeleteReservation() {
        // Arrange
        given(repository.existsById(reservationId)).willReturn(true);

        // Act
        reservationService.deleteReservation(reservationId);

        // Assert
        verify(repository).deleteById(reservationId);
    }
}