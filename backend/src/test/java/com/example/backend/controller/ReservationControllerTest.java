package com.example.backend.controller;

import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.repository.ReservationRepository;
import com.example.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@DisplayName("Reservation Controller Integration Tests")
class ReservationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ReservationRepository reservationRepository;

    private String authToken;
    private LocalDateTime now;
    private LocalDateTime twoHoursLater;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_DISPLAY_NAME = "Test User";
    private static final String ROOM_ID = "room-1";

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        cleanupTestData();

        now = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS);
        twoHoursLater = now.plusHours(2);

        authToken = registerAndLogin();
        assertNotNull(authToken, "Auth token should not be null");
    }

    private void cleanupTestData() {
        reservationRepository.deleteAll();
        if (userRepository.existsByEmail(TEST_EMAIL)) {
            userRepository.deleteByEmail(TEST_EMAIL);
        }
    }

    private String registerAndLogin() throws Exception {
        RegisterRequest registerRequest =
                new RegisterRequest(TEST_EMAIL, TEST_DISPLAY_NAME, TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private String createReservation(String roomId, LocalDateTime start, LocalDateTime end, String user)
            throws Exception {

        CreateReservationRequest req = new CreateReservationRequest(roomId, start, end, user);

        MvcResult result = mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText();
    }

    // ===============================
    // CREATE
    // ===============================

    @Test
    @Order(1)
    void shouldCreateReservation() throws Exception {
        CreateReservationRequest req =
                new CreateReservationRequest(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.roomId").value(ROOM_ID));
    }

    @Test
    void shouldRejectPastReservation() throws Exception {
        CreateReservationRequest req =
                new CreateReservationRequest(
                        ROOM_ID,
                        LocalDateTime.now().minusHours(2),
                        LocalDateTime.now().minusHours(1),
                        TEST_DISPLAY_NAME);

        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ===============================
    // GET BY ROOM
    // ===============================

    @Test
    void shouldReturnReservationsByRoom() throws Exception {
        createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

        mockMvc.perform(get("/api/reservations/{roomId}", ROOM_ID)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ===============================
    // UPDATE
    // ===============================

    @Test
    void shouldUpdateReservation() throws Exception {
        String id = createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

        LocalDateTime newStart = now.plusHours(3);
        LocalDateTime newEnd = newStart.plusHours(2);

        CreateReservationRequest update =
                new CreateReservationRequest(ROOM_ID, newStart, newEnd, TEST_DISPLAY_NAME);

        mockMvc.perform(put("/api/reservations/{id}", id)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    // ===============================
    // DELETE
    // ===============================

    @Test
    void shouldDeleteReservation() throws Exception {
        String id = createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

        mockMvc.perform(delete("/api/reservations/{id}", id)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }
}