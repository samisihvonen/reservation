package com.example.backend.controller;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private CreateReservationRequest request;
    private ReservationResponse response;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);

        request = new CreateReservationRequest("ROOM1", start, end, "test-user");
        response = new ReservationResponse(
                "res-1",
                "ROOM1",
                start,
                end,
                "test-user",
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    void create_ShouldReturnCreatedReservation() {
        when(reservationService.createReservation(any(CreateReservationRequest.class))).thenReturn(response);

        ResponseEntity<ReservationResponse> result = reservationController.create(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void update_ShouldReturnUpdatedReservation() {
        when(reservationService.updateReservation(anyString(), any(CreateReservationRequest.class)))
                .thenReturn(response);

        ResponseEntity<ReservationResponse> result = reservationController.update("res-1", request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void delete_ShouldReturnNoContent() {
        ResponseEntity<Void> result = reservationController.delete("res-1");

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(reservationService).deleteReservation("res-1");
    }

    @Test
    void getById_ShouldReturnReservation() {
        when(reservationService.getReservationById("res-1")).thenReturn(response);

        ResponseEntity<ReservationResponse> result = reservationController.getById("res-1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getByRoom_ShouldReturnReservationList() {
        when(reservationService.getReservationsByRoom("ROOM1")).thenReturn(List.of(response));

        ResponseEntity<List<ReservationResponse>> result = reservationController.getByRoom("ROOM1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }
}
