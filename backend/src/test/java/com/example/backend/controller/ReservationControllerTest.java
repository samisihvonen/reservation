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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationControllerTest {

    @Mock
    private ReservationService service;

    @InjectMocks
    private ReservationController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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

        when(service.createReservation(any(CreateReservationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("res123"))
                .andExpect(jsonPath("$.roomId").value("room123"))
                .andExpect(jsonPath("$.guestName").value("John Doe"));

        verify(service).createReservation(any(CreateReservationRequest.class));
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when create reservation request is invalid")
    void createReservation_shouldReturnBadRequestWhenInvalid() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId("");
        request.setStartDate(null);
        request.setEndDate(LocalDate.now().plusDays(2));
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

        when(service.updateReservation(anyString(), any(CreateReservationRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/reservations/{id}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.guestName").value("John Doe Updated"));

        verify(service).updateReservation(anyString(), any(CreateReservationRequest.class));
    }

    @Test
    @DisplayName("Should return NOT_FOUND when updating non-existent reservation")
    void updateReservation_shouldReturnNotFoundWhenReservationDoesNotExist() throws Exception {
        String reservationId = "nonexistent";
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId("room123");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setGuestName("John Doe");

        when(service.updateReservation(anyString(), any(CreateReservationRequest.class)))
                .thenThrow(new RuntimeException("Reservation not found"));

        mockMvc.perform(put("/api/reservations/{id}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(service).updateReservation(anyString(), any(CreateReservationRequest.class));
    }

    @Test
    @DisplayName("Should delete reservation and return NO_CONTENT status")
    void deleteReservation_shouldReturnNoContentStatus() throws Exception {
        String reservationId = "res123";
        doNothing().when(service).deleteReservation(anyString());

        mockMvc.perform(delete("/api/reservations/{id}", reservationId))
                .andExpect(status().isNoContent());

        verify(service).deleteReservation(anyString());
    }

    @Test
    @DisplayName("Should return NOT_FOUND when deleting non-existent reservation")
    void deleteReservation_shouldReturnNotFoundWhenReservationDoesNotExist() throws Exception {
        String reservationId = "nonexistent";
        doThrow(new RuntimeException("Reservation not found")).when(service).deleteReservation(anyString());

        mockMvc.perform(delete("/api/reservations/{id}", reservationId))
                .andExpect(status().isNotFound());

        verify(service).deleteReservation(anyString());
    }

    @Test
    @DisplayName("Should return reservation by ID with OK status")
    void getById_shouldReturnReservationWithOkStatus() throws Exception {
        String reservationId = "res123";
        ReservationResponse response = new ReservationResponse();
        response.setId(reservationId);
        response.setRoomId("room123");
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe");

        when(service.getReservationById(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/reservations/detail/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.roomId").value("room123"))
                .andExpect(jsonPath("$.guestName").value("John Doe"));

        verify(service).getReservationById(anyString());
    }

    @Test
    @DisplayName("Should return NOT_FOUND when reservation by ID does not exist")
    void getById_shouldReturnNotFoundWhenReservationDoesNotExist() throws Exception {
        String reservationId = "nonexistent";
        when(service.getReservationById(anyString())).thenThrow(new RuntimeException("Reservation not found"));

        mockMvc.perform(get("/api/reservations/detail/{id}", reservationId))
                .andExpect(status().isNotFound());

        verify(service).getReservationById(anyString());
    }

    @Test
    @DisplayName("Should return list of reservations by room with OK status")
    void getByRoom_shouldReturnReservationsWithOkStatus() throws Exception {
        String roomId = "room123";
        ReservationResponse response = new ReservationResponse();
        response.setId("res123");
        response.setRoomId(roomId);
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe");

        when(service.getReservationsByRoom(anyString())).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/reservations/{roomId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("res123"))
                .andExpect(jsonPath("$[0].roomId").value(roomId))
                .andExpect(jsonPath("$[0].guestName").value("John Doe"));

        verify(service).getReservationsByRoom(anyString());
    }

    @Test
    @DisplayName("Should return empty list when no reservations exist for room")
    void getByRoom_shouldReturnEmptyListWhenNoReservationsExist() throws Exception {
        String roomId = "emptyroom";
        when(service.getReservationsByRoom(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reservations/{roomId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).getReservationsByRoom(anyString());
    }

    @Test
    @DisplayName("Should return ResponseEntity with reservation when create is called directly")
    void create_shouldReturnResponseEntityWithReservation() {
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

        when(service.createReservation(any(CreateReservationRequest.class))).thenReturn(response);

        ResponseEntity<ReservationResponse> result = controller.create(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo("res123");
        assertThat(result.getBody().getGuestName()).isEqualTo("John Doe");

        verify(service).createReservation(any(CreateReservationRequest.class));
    }

    @Test
    @DisplayName("Should return ResponseEntity with updated reservation when update is called directly")
    void update_shouldReturnResponseEntityWithUpdatedReservation() {
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

        when(service.updateReservation(anyString(), any(CreateReservationRequest.class))).thenReturn(response);

        ResponseEntity<ReservationResponse> result = controller.update(reservationId, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(reservationId);
        assertThat(result.getBody().getGuestName()).isEqualTo("John Doe Updated");

        verify(service).updateReservation(anyString(), any(CreateReservationRequest.class));
    }

    @Test
    @DisplayName("Should return ResponseEntity with NO_CONTENT when delete is called directly")
    void delete_shouldReturnResponseEntityWithNoContent() {
        String reservationId = "res123";
        doNothing().when(service).deleteReservation(anyString());

        ResponseEntity<Void> result = controller.delete(reservationId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();

        verify(service).deleteReservation(anyString());
    }

    @Test
    @DisplayName("Should return ResponseEntity with reservation when getById is called directly")
    void getById_shouldReturnResponseEntityWithReservation() {
        String reservationId = "res123";
        ReservationResponse response = new ReservationResponse();
        response.setId(reservationId);
        response.setRoomId("room123");
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe");

        when(service.getReservationById(anyString())).thenReturn(response);

        ResponseEntity<ReservationResponse> result = controller.getById(reservationId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(reservationId);
        assertThat(result.getBody().getGuestName()).isEqualTo("John Doe");

        verify(service).getReservationById(anyString());
    }

    @Test
    @DisplayName("Should return ResponseEntity with list of reservations when getByRoom is called directly")
    void getByRoom_shouldReturnResponseEntityWithReservations() {
        String roomId = "room123";
        ReservationResponse response = new ReservationResponse();
        response.setId("res123");
        response.setRoomId(roomId);
        response.setStartDate(LocalDate.now());
        response.setEndDate(LocalDate.now().plusDays(2));
        response.setGuestName("John Doe");

        when(service.getReservationsByRoom(anyString())).thenReturn(Collections.singletonList(response));

        ResponseEntity<List<ReservationResponse>> result = controller.getByRoom(roomId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).getId()).isEqualTo("res123");
        assertThat(result.getBody().get(0).getGuestName()).isEqualTo("John Doe");

        verify(service).getReservationsByRoom(anyString());
    }
}