package com.example.backend.controller;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.service.ReservationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReservationControllerTest {

    private static class StubReservationService extends ReservationService {
        private ReservationResponse response;
        private String deletedId;
        private String requestedId;
        private String requestedRoomId;

        StubReservationService() {
            super(null);
        }

        @Override
        public ReservationResponse createReservation(CreateReservationRequest request) {
            return response;
        }

        @Override
        public ReservationResponse updateReservation(String id, CreateReservationRequest request) {
            requestedId = id;
            return response;
        }

        @Override
        public void deleteReservation(String id) {
            deletedId = id;
        }

        @Override
        public ReservationResponse getReservationById(String id) {
            requestedId = id;
            return response;
        }

        @Override
        public List<ReservationResponse> getReservationsByRoom(String roomId) {
            requestedRoomId = roomId;
            return List.of(response);
        }
    }

    private StubReservationService reservationService;
    private ReservationController reservationController;
    private String testId;
    private CreateReservationRequest testRequest;
    private ReservationResponse testResponse;

    @BeforeEach
    void setUp() {
        reservationService = new StubReservationService();
        reservationController = new ReservationController(reservationService);

        testId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);

        testRequest = new CreateReservationRequest("ROOM1", startTime, endTime, "Test User");
        testResponse = new ReservationResponse(
                testId, "ROOM1", startTime, endTime, "Test User",
                LocalDateTime.now(), LocalDateTime.now());

        reservationService.response = testResponse;
    }

    @Test
    void create_shouldReturnCreatedReservation_whenValidRequest() {
        ResponseEntity<ReservationResponse> response = reservationController.create(testRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
    }

    @Test
    void update_shouldReturnUpdatedReservation_whenValidRequestAndId() {
        ResponseEntity<ReservationResponse> response = reservationController.update(testId, testRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
        assertEquals(testId, reservationService.requestedId);
    }

    @Test
    void delete_shouldReturnNoContent_whenValidId() {
        ResponseEntity<Void> response = reservationController.delete(testId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(testId, reservationService.deletedId);
    }

    @Test
    void getById_shouldReturnReservation_whenValidId() {
        ResponseEntity<ReservationResponse> response = reservationController.getById(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
        assertEquals(testId, reservationService.requestedId);
    }

    @Test
    void getByRoom_shouldReturnReservations_whenValidRoomId() {
        ResponseEntity<List<ReservationResponse>> response = reservationController.getByRoom("ROOM1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ROOM1", reservationService.requestedRoomId);
    }
}