// ============================================
// Backend Unit Tests - Reservation Service
// ============================================
// File: src/test/java/com/example/backend/service/ReservationServiceTest.java

package com.example.backend.service;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.dto.ReservationResponse;
import com.example.backend.exception.InvalidReservationTimeException;
import com.example.backend.exception.RoomAlreadyBookedException;
import com.example.backend.model.Reservation;
import com.example.backend.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ReservationService Unit Tests")
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private CreateReservationRequest validRequest;
    private LocalDateTime now;
    private LocalDateTime twoHoursLater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        now = LocalDateTime.now().plusHours(1);
        twoHoursLater = now.plusHours(2);

        validRequest = new CreateReservationRequest(
                "room-1",
                now,
                twoHoursLater,
                "Test User"
        );
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void testCreateReservationSuccess() {
        when(reservationRepository.findByRoomId("room-1")).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation res = invocation.getArgument(0);
            res.setId("test-id-123");
            return res;
        });

        ReservationResponse response = reservationService.createReservation(validRequest);

        assertNotNull(response);
        assertEquals("room-1", response.getRoomId());
        assertEquals("Test User", response.getUser());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception for past reservation")
    void testCreateReservationInPast() {
        CreateReservationRequest pastRequest = new CreateReservationRequest(
                "room-1",
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                "Test User"
        );

        assertThrows(InvalidReservationTimeException.class, () -> {
            reservationService.createReservation(pastRequest);
        });
    }

    @Test
    @DisplayName("Should throw exception when end time is before start time")
    void testCreateReservationEndTimeBeforeStartTime() {
        CreateReservationRequest invalidRequest = new CreateReservationRequest(
                "room-1",
                twoHoursLater,
                now,
                "Test User"
        );

        assertThrows(InvalidReservationTimeException.class, () -> {
            reservationService.createReservation(invalidRequest);
        });
    }

    @Test
    @DisplayName("Should throw exception when room is already booked")
    void testCreateReservationRoomAlreadyBooked() {
        Reservation existingReservation = new Reservation(
                "room-1",
                now,
                twoHoursLater,
                "Another User"
        );

        when(reservationRepository.findByRoomId("room-1"))
                .thenReturn(Arrays.asList(existingReservation));

        assertThrows(RoomAlreadyBookedException.class, () -> {
            reservationService.createReservation(validRequest);
        });
    }

    @Test
    @DisplayName("Should retrieve reservations by room ID")
    void testGetReservationsByRoom() {
        Reservation res1 = new Reservation("room-1", now, twoHoursLater, "User 1");
        Reservation res2 = new Reservation("room-1", twoHoursLater.plusHours(1), 
                                          twoHoursLater.plusHours(3), "User 2");

        when(reservationRepository.findByRoomId("room-1"))
                .thenReturn(Arrays.asList(res1, res2));

        List<ReservationResponse> responses = reservationService.getReservationsByRoom("room-1");

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("User 1", responses.get(0).getUser());
    }

    @Test
    @DisplayName("Should delete reservation by ID")
    void testDeleteReservation() {
        when(reservationRepository.existsById("test-id-123")).thenReturn(true);

        reservationService.deleteReservation("test-id-123");

        verify(reservationRepository, times(1)).deleteById("test-id-123");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent reservation")
    void testDeleteNonExistentReservation() {
        when(reservationRepository.existsById("non-existent")).thenReturn(false);

        assertThrows(Exception.class, () -> {
            reservationService.deleteReservation("non-existent");
        });
    }

    @Test
    @DisplayName("Should detect overlapping reservations")
    void testOverlappingReservations() {
        // Existing reservation: 10:00 - 12:00
        Reservation existing = new Reservation(
                "room-1",
                now,
                twoHoursLater,
                "User 1"
        );

        // New request: 11:00 - 13:00 (overlaps)
        CreateReservationRequest overlappingRequest = new CreateReservationRequest(
                "room-1",
                now.plusHours(1),
                twoHoursLater.plusHours(1),
                "User 2"
        );

        when(reservationRepository.findByRoomId("room-1"))
                .thenReturn(Arrays.asList(existing));

        assertThrows(RoomAlreadyBookedException.class, () -> {
            reservationService.createReservation(overlappingRequest);
        });
    }

    @Test
    @DisplayName("Should allow non-overlapping consecutive reservations")
    void testNonOverlappingConsecutiveReservations() {
        // Existing: 10:00 - 12:00
        Reservation existing = new Reservation(
                "room-1",
                now,
                twoHoursLater,
                "User 1"
        );

        // New: 12:00 - 14:00 (right after, no overlap)
        CreateReservationRequest consecutiveRequest = new CreateReservationRequest(
                "room-1",
                twoHoursLater,
                twoHoursLater.plusHours(2),
                "User 2"
        );

        when(reservationRepository.findByRoomId("room-1"))
                .thenReturn(Arrays.asList(existing));

        // Should NOT throw exception
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation res = invocation.getArgument(0);
            res.setId("test-id-456");
            return res;
        });

        assertDoesNotThrow(() -> {
            reservationService.createReservation(consecutiveRequest);
        });
    }
}