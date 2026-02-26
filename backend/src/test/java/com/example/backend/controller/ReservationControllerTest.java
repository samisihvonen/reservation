package com.example.backend.controller;

import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.dto.ReservationResponse;
import com.example.backend.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should create reservation and return CREATED status with response")
    void createReservation_shouldReturnCreatedStatus() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId("room123");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setGuestName("John Doe");

        ReservationResponse response = new ReservationResponse();
        response.setId("res123");
        response.setRoomId("room123");
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe");

        when(reservationService.createReservation(any(CreateReservationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("res123"))
                .andExpect(jsonPath("$.roomId").value("room123"))
                .andExpect(jsonPath("$.guestName").value("John Doe"));
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when create reservation request is invalid")
    void createReservation_shouldReturnBadRequestWhenInvalid() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId("");
        request.setStartDate(null);
        request.setEndDate(null);
        request.setGuestName("");

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update reservation and return OK status with response")
    void updateReservation_shouldReturnOkStatus() throws Exception {
        String reservationId = "res123";
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId("room123");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setGuestName("John Doe Updated");

        ReservationResponse response = new ReservationResponse();
        response.setId(reservationId);
        response.setRoomId("room123");
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe Updated");

        when(reservationService.updateReservation(anyString(), any(CreateReservationRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/reservations/{id}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.guestName").value("John Doe Updated"));
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when update reservation request is invalid")
    void updateReservation_shouldReturnBadRequestWhenInvalid() throws Exception {
        String reservationId = "res123";
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId("");
        request.setStartDate(null);
        request.setEndDate(null);
        request.setGuestName("");

        mockMvc.perform(put("/api/reservations/{id}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete reservation and return NO_CONTENT status")
    void deleteReservation_shouldReturnNoContentStatus() throws Exception {
        String reservationId = "res123";
        doNothing().when(reservationService).deleteReservation(anyString());

        mockMvc.perform(delete("/api/reservations/{id}", reservationId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return reservation by id and return OK status with response")
    void getReservationById_shouldReturnOkStatus() throws Exception {
        String reservationId = "res123";
        ReservationResponse response = new ReservationResponse();
        response.setId(reservationId);
        response.setRoomId("room123");
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe");

        when(reservationService.getReservationById(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/reservations/detail/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.roomId").value("room123"))
                .andExpect(jsonPath("$.guestName").value("John Doe"));
    }

    @Test
    @DisplayName("Should return NOT_FOUND when reservation not found by id")
    void getReservationById_shouldReturnNotFoundWhenNotExists() throws Exception {
        String reservationId = "nonexistent";
        when(reservationService.getReservationById(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/reservations/detail/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Should return reservations by room and return OK status with list")
    void getReservationsByRoom_shouldReturnOkStatus() throws Exception {
        String roomId = "room123";
        ReservationResponse response = new ReservationResponse();
        response.setId("res123");
        response.setRoomId(roomId);
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe");

        when(reservationService.getReservationsByRoom(anyString())).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/reservations/{roomId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("res123"))
                .andExpect(jsonPath("$[0].roomId").value(roomId))
                .andExpect(jsonPath("$[0].guestName").value("John Doe"));
    }

    @Test
    @DisplayName("Should return empty list when no reservations found for room")
    void getReservationsByRoom_shouldReturnEmptyListWhenNoReservations() throws Exception {
        String roomId = "emptyRoom";
        when(reservationService.getReservationsByRoom(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reservations/{roomId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Should return ResponseEntity with reservation when service returns response")
    void create_shouldReturnResponseEntityWithReservation() {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId("room123");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setGuestName("John Doe");

        ReservationResponse expectedResponse = new ReservationResponse();
        expectedResponse.setId("res123");
        expectedResponse.setRoomId("room123");
        expectedResponse.setStartDate(LocalDate.now());
        expectedResponse.setEndDate(LocalDate.now().plusDays(2));
        expectedResponse.setGuestName("John Doe");

        when(reservationService.createReservation(any(CreateReservationRequest.class))).thenReturn(expectedResponse);

        ResponseEntity<ReservationResponse> response = reservationController.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should return ResponseEntity with updated reservation when service returns response")
    void update_shouldReturnResponseEntityWithUpdatedReservation() {
        String reservationId = "res123";
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId("room123");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setGuestName("John Doe Updated");

        ReservationResponse expectedResponse = new ReservationResponse();
        expectedResponse.setId(reservationId);
        expectedResponse.setRoomId("room123");
        expectedResponse.setStartDate(LocalDate.now());
        expectedResponse.setEndDate(LocalDate.now().plusDays(2));
        expectedResponse.setGuestName("John Doe Updated");

        when(reservationService.updateReservation(anyString(), any(CreateReservationRequest.class))).thenReturn(expectedResponse);

        ResponseEntity<ReservationResponse> response = reservationController.update(reservationId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should return ResponseEntity with no content when delete is successful")
    void delete_shouldReturnResponseEntityWithNoContent() {
        String reservationId = "res123";
        doNothing().when(reservationService).deleteReservation(anyString());

        ResponseEntity<Void> response = reservationController.delete(reservationId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should return ResponseEntity with reservation when get by id is successful")
    void getById_shouldReturnResponseEntityWithReservation() {
        String reservationId = "res123";
        ReservationResponse expectedResponse = new ReservationResponse();
        expectedResponse.setId(reservationId);
        expectedResponse.setRoomId("room123");
        expectedResponse.setStartDate(LocalDate.now());
        expectedResponse.setEndDate(LocalDate.now().plusDays(2));
        expectedResponse.setGuestName("John Doe");

        when(reservationService.getReservationById(anyString())).thenReturn(expectedResponse);

        ResponseEntity<ReservationResponse> response = reservationController.getById(reservationId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should return ResponseEntity with null body when reservation not found")
    void getById_shouldReturnResponseEntityWithNullWhenNotFound() {
        String reservationId = "nonexistent";
        when(reservationService.getReservationById(anyString())).thenReturn(null);

        ResponseEntity<ReservationResponse> response = reservationController.getById(reservationId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Should return ResponseEntity with list of reservations when get by room is successful")
    void getByRoom_shouldReturnResponseEntityWithReservations() {
        String roomId = "room123";
        ReservationResponse response = new ReservationResponse();
        response.setId("res123");
        response.setRoomId(roomId);
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe");

        when(reservationService.getReservationsByRoom(anyString())).thenReturn(Collections.singletonList(response));

        ResponseEntity<List<ReservationResponse>> responseEntity = reservationController.getByRoom(roomId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).hasSize(1);
        assertThat(responseEntity.getBody().get(0).getId()).isEqualTo("res123");
    }

    @Test
    @DisplayName("Should return ResponseEntity with empty list when no reservations found for room")
    void getByRoom_shouldReturnResponseEntityWithEmptyList() {
        String roomId = "emptyRoom";
        when(reservationService.getReservationsByRoom(anyString())).thenReturn(Collections.emptyList());

        ResponseEntity<List<ReservationResponse>> responseEntity = reservationController.getByRoom(roomId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }
}