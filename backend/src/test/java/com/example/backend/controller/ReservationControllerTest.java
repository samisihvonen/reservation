package com.example.backend.controller;

import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.dto.ReservationResponse;
import com.example.backend.service.ReservationService;
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

    private CreateReservationRequest createReservationRequest;
    private ReservationResponse reservationResponse;

    @BeforeEach
    void setUp() {
        createReservationRequest = new CreateReservationRequest(
                "user123",
                "2023-12-25",
                "18:00",
                4,
                "Special request"
        );

        reservationResponse = new ReservationResponse(
                "res123",
                "user123",
                "2023-12-25",
                "18:00",
                4,
                "Special request",
                "CONFIRMED"
        );
    }

    @Test
    void create_ShouldReturnCreatedReservation_WhenRequestIsValid() {
        when(reservationService.create(any(CreateReservationRequest.class)))
                .thenReturn(reservationResponse);

        ResponseEntity<ReservationResponse> response = reservationController.create(createReservationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reservationResponse, response.getBody());
        verify(reservationService, times(1)).create(any(CreateReservationRequest.class));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenRequestIsInvalid() {
        CreateReservationRequest invalidRequest = new CreateReservationRequest(
                null,
                null,
                null,
                0,
                null
        );

        when(reservationService.create(any(CreateReservationRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        ResponseEntity<ReservationResponse> response = reservationController.create(invalidRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(reservationService, times(1)).create(any(CreateReservationRequest.class));
    }

    @Test
    void update_ShouldReturnUpdatedReservation_WhenIdAndRequestAreValid() {
        String reservationId = "res123";
        when(reservationService.update(anyString(), any(CreateReservationRequest.class)))
                .thenReturn(reservationResponse);

        ResponseEntity<ReservationResponse> response = reservationController.update(reservationId, createReservationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservationResponse, response.getBody());
        verify(reservationService, times(1)).update(anyString(), any(CreateReservationRequest.class));
    }

    @Test
    void update_ShouldReturnNotFound_WhenReservationDoesNotExist() {
        String nonExistentId = "nonExistentId";
        when(reservationService.update(anyString(), any(CreateReservationRequest.class)))
                .thenThrow(new RuntimeException("Reservation not found"));

        ResponseEntity<ReservationResponse> response = reservationController.update(nonExistentId, createReservationRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(reservationService, times(1)).update(anyString(), any(CreateReservationRequest.class));
    }

    @Test
    void delete_ShouldReturnNoContent_WhenReservationExists() {
        String reservationId = "res123";
        doNothing().when(reservationService).delete(anyString());

        ResponseEntity<Void> response = reservationController.delete(reservationId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService, times(1)).delete(anyString());
    }

    @Test
    void delete_ShouldReturnNotFound_WhenReservationDoesNotExist() {
        String nonExistentId = "nonExistentId";
        doThrow(new RuntimeException("Reservation not found")).when(reservationService).delete(anyString());

        ResponseEntity<Void> response = reservationController.delete(nonExistentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(reservationService, times(1)).delete(anyString());
    }

    @Test
    void getById_ShouldReturnReservation_WhenReservationExists() {
        String reservationId = "res123";
        when(reservationService.getById(anyString())).thenReturn(reservationResponse);

        ResponseEntity<ReservationResponse> response = reservationController.getById(reservationId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservationResponse, response.getBody());
        verify(reservationService, times(1)).getById(anyString());
    }

    @Test
    void getById_ShouldReturnNotFound_WhenReservationDoesNotExist() {
        String nonExistentId = "nonExistentId";
        when(reservationService.getById(anyString())).thenThrow(new RuntimeException("Reservation not found"));

        ResponseEntity<ReservationResponse> response = reservationController.getById(nonExistentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(reservationService, times(1)).getById(anyString());
    }
}