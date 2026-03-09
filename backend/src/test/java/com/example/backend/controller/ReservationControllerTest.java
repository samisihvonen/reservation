package com.example.backend.controller;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReservationControllerTest {

    @MockBean
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String baseUrl = "/api/reservations";
    private String testId;
    private CreateReservationRequest testRequest;
    private ReservationResponse testResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
        objectMapper = new ObjectMapper();
        testId = UUID.randomUUID().toString();

        testRequest = new CreateReservationRequest(
                "Test User",
                "test@example.com",
                "2023-12-31",
                "12:00",
                2
        );

        testResponse = new ReservationResponse(
                testId,
                "Test User",
                "test@example.com",
                "2023-12-31",
                "12:00",
                2,
                "CONFIRMED"
        );
    }

    @Test
    void create_shouldReturnCreatedReservation_whenValidRequest() throws Exception {
        given(reservationService.create(any(CreateReservationRequest.class))).willReturn(testResponse);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId))
                .andExpect(jsonPath("$.customerName").value(testRequest.getCustomerName()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void create_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        CreateReservationRequest invalidRequest = new CreateReservationRequest(
                "",
                "invalid-email",
                "2023-02-30",
                "25:00",
                0
        );

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnUpdatedReservation_whenValidRequestAndId() throws Exception {
        given(reservationService.update(anyString(), any(CreateReservationRequest.class))).willReturn(testResponse);

        mockMvc.perform(put(baseUrl + "/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId))
                .andExpect(jsonPath("$.customerName").value(testRequest.getCustomerName()));
    }

    @Test
    void update_shouldReturnNotFound_whenInvalidId() throws Exception {
        String invalidId = "invalid-id";
        given(reservationService.update(anyString(), any(CreateReservationRequest.class)))
                .willThrow(new RuntimeException("Reservation not found"));

        mockMvc.perform(put(baseUrl + "/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnNoContent_whenValidId() throws Exception {
        doNothing().when(reservationService).delete(anyString());

        mockMvc.perform(delete(baseUrl + "/{id}", testId))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturnNotFound_whenInvalidId() throws Exception {
        String invalidId = "invalid-id";
        doNothing().when(reservationService).delete(invalidId);

        mockMvc.perform(delete(baseUrl + "/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnReservation_whenValidId() throws Exception {
        given(reservationService.getById(anyString())).willReturn(testResponse);

        mockMvc.perform(get(baseUrl + "/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId))
                .andExpect(jsonPath("$.customerName").value(testResponse.getCustomerName()));
    }

    @Test
    void getById_shouldReturnNotFound_whenInvalidId() throws Exception {
        String invalidId = "invalid-id";
        given(reservationService.getById(invalidId)).willThrow(new RuntimeException("Reservation not found"));

        mockMvc.perform(get(baseUrl + "/{id}", invalidId))
                .andExpect(status().isNotFound());
    }
}