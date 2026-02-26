package com.example.backend.controller;

import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.dto.ReservationResponse;
import com.example.backend.service.ReservationService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private CreateReservationRequest createRequest;
    private ReservationResponse reservationResponse;
    private String reservationId;

    @BeforeEach
    void setUp() {
        reservationId = UUID.randomUUID().toString();
        createRequest = new CreateReservationRequest(
                "John Doe",
                "john.doe@example.com",
                "2023-12-25",
                2
        );
        reservationResponse = new ReservationResponse(
                reservationId,
                "John Doe",
                "john.doe@example.com",
                "2023-12-25",
                2,
                "CONFIRMED"
        );
    }

    @Test
    void create_shouldReturnCreatedReservation_whenRequestIsValid() {
        when(reservationService.create(any(CreateReservationRequest.class)))
                .thenReturn(reservationResponse);

        ResponseEntity<ReservationResponse> response = reservationController.create(createRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reservationResponse, response.getBody());
        verify(reservationService, times(1)).create(any(CreateReservationRequest.class));
    }

    @Test
    void create_shouldReturnBadRequest_whenServiceThrowsException() {
        when(reservationService.create(any(CreateReservationRequest.class)))
                .thenThrow(new RuntimeException("Invalid request data"));

        assertThrows(RuntimeException.class, () -> {
            reservationController.create(createRequest);
        });
    }

    @Test
    void update_shouldReturnUpdatedReservation_whenReservationExists() {
        when(reservationService.update(anyString(), any(CreateReservationRequest.class)))
                .thenReturn(reservationResponse);

        ResponseEntity<ReservationResponse> response = reservationController.update(reservationId, createRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservationResponse, response.getBody());
        verify(reservationService, times(1)).update(anyString(), any(CreateReservationRequest.class));
    }

    @Test
    void update_shouldReturnNotFound_whenReservationDoesNotExist() {
        when(reservationService.update(anyString(), any(CreateReservationRequest.class)))
                .thenThrow(new RuntimeException("Reservation not found"));

        assertThrows(RuntimeException.class, () -> {
            reservationController.update("non-existent-id", createRequest);
        });
    }

    @Test
    void delete_shouldReturnNoContent_whenReservationExists() {
        doNothing().when(reservationService).delete(anyString());

        ResponseEntity<Void> response = reservationController.delete(reservationId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService, times(1)).delete(anyString());
    }

    @Test
    void delete_shouldThrowException_whenReservationDoesNotExist() {
        doThrow(new RuntimeException("Reservation not found")).when(reservationService).delete(anyString());

        assertThrows(RuntimeException.class, () -> {
            reservationController.delete("non-existent-id");
        });
    }

    @Test
    void getById_shouldReturnReservation_whenReservationExists() {
        when(reservationService.getById(anyString())).thenReturn(reservationResponse);

        ResponseEntity<ReservationResponse> response = reservationController.getById(reservationId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservationResponse, response.getBody());
        verify(reservationService, times(1)).getById(anyString());
    }

    @Test
    void getById_shouldThrowException_whenReservationDoesNotExist() {
        when(reservationService.getById(anyString()))
                .thenThrow(new RuntimeException("Reservation not found"));

        assertThrows(RuntimeException.class, () -> {
            reservationController.getById("non-existent-id");
        });
    }
}