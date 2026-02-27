package com.example.backend.controller;

import com.example.backend.model.request.CreateReservationRequest;
import com.example.backend.model.response.ReservationResponse;
import com.example.backend.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturnCreatedReservation_whenRequestIsValid() throws Exception {
        // Given
        CreateReservationRequest request = new CreateReservationRequest(
                "user123",
                "2023-12-25",
                "12:00",
                4
        );

        ReservationResponse expectedResponse = new ReservationResponse(
                UUID.randomUUID().toString(),
                "user123",
                "2023-12-25",
                "12:00",
                4,
                "CONFIRMED"
        );

        given(reservationService.create(any(CreateReservationRequest.class)))
                .willReturn(ResponseEntity.ok(expectedResponse));

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(expectedResponse.getUserId()))
                .andExpect(jsonPath("$.date").value(expectedResponse.getDate()))
                .andExpect(jsonPath("$.time").value(expectedResponse.getTime()))
                .andExpect(jsonPath("$.partySize").value(expectedResponse.getPartySize()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()));
    }

    @Test
    void create_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // Given
        CreateReservationRequest invalidRequest = new CreateReservationRequest(
                "",
                "",
                "",
                0
        );

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnUpdatedReservation_whenRequestIsValid() throws Exception {
        // Given
        String reservationId = UUID.randomUUID().toString();
        CreateReservationRequest request = new CreateReservationRequest(
                "user123",
                "2023-12-26",
                "13:00",
                5
        );

        ReservationResponse expectedResponse = new ReservationResponse(
                reservationId,
                "user123",
                "2023-12-26",
                "13:00",
                5,
                "UPDATED"
        );

        given(reservationService.update(anyString(), any(CreateReservationRequest.class)))
                .willReturn(ResponseEntity.ok(expectedResponse));

        // When & Then
        mockMvc.perform(put("/api/reservations/{id}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.date").value(expectedResponse.getDate()))
                .andExpect(jsonPath("$.time").value(expectedResponse.getTime()))
                .andExpect(jsonPath("$.partySize").value(expectedResponse.getPartySize()));
    }

    @Test
    void update_shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
        // Given
        String nonExistentId = UUID.randomUUID().toString();
        CreateReservationRequest request = new CreateReservationRequest(
                "user123",
                "2023-12-26",
                "13:00",
                5
        );

        given(reservationService.update(anyString(), any(CreateReservationRequest.class)))
                .willReturn(ResponseEntity.notFound().build());

        // When & Then
        mockMvc.perform(put("/api/reservations/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnNoContent_whenReservationExists() throws Exception {
        // Given
        String reservationId = UUID.randomUUID().toString();
        willDoNothing().given(reservationService).delete(reservationId);

        // When & Then
        mockMvc.perform(delete("/api/reservations/{id}", reservationId))
                .andExpect(status().isNoContent());

        verify(reservationService).delete(reservationId);
    }

    @Test
    void delete_shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
        // Given
        String nonExistentId = UUID.randomUUID().toString();
        given(reservationService.delete(nonExistentId))
                .willThrow(new RuntimeException("Reservation not found"));

        // When & Then
        mockMvc.perform(delete("/api/reservations/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnReservation_whenReservationExists() throws Exception {
        // Given
        String reservationId = UUID.randomUUID().toString();
        ReservationResponse expectedResponse = new ReservationResponse(
                reservationId,
                "user123",
                "2023-12-25",
                "12:00",
                4,
                "CONFIRMED"
        );

        given(reservationService.getById(reservationId))
                .willReturn(ResponseEntity.ok(expectedResponse));

        // When & Then
        mockMvc.perform(get("/api/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.userId").value(expectedResponse.getUserId()))
                .andExpect(jsonPath("$.date").value(expectedResponse.getDate()))
                .andExpect(jsonPath("$.time").value(expectedResponse.getTime()))
                .andExpect(jsonPath("$.partySize").value(expectedResponse.getPartySize()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()));
    }

    @Test
    void getById_shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
        // Given
        String nonExistentId = UUID.randomUUID().toString();
        given(reservationService.getById(nonExistentId))
                .willReturn(ResponseEntity.notFound().build());

        // When & Then
        mockMvc.perform(get("/api/reservations/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}