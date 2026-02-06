// ============================================
// Extended Integration Tests - Reservation Controller
// ============================================
// File: src/test/java/com/example/backend/controller/ReservationControllerTest.java

package com.example.backend.controller;


import java.util.List;
import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.repository.ReservationRepository;
import com.example.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Reservation Controller Integration Tests")
@TestPropertySource(locations = "classpath:application-test.properties")
@SuppressWarnings({ "null", "unchecked" })
class ReservationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ReservationRepository reservationRepository;

        private String authToken;
        private LocalDateTime now;
        private LocalDateTime twoHoursLater;

        private static final String TEST_EMAIL = "test@example.com";
        private static final String TEST_PASSWORD = "password123";
        private static final String TEST_DISPLAY_NAME = "Test User";
        private static final String ROOM_ID = "room-1";

        @BeforeEach
        void setUp() throws Exception {
                cleanupTestData();

                now = LocalDateTime.now().plusHours(1);
                twoHoursLater = now.plusHours(2);

                authToken = registerAndLogin();
        }

        private void cleanupTestData() {
                reservationRepository.deleteAll();
                userRepository.deleteByEmail(TEST_EMAIL);
        }

        private String registerAndLogin() throws Exception {
                RegisterRequest registerRequest = new RegisterRequest(TEST_EMAIL, TEST_DISPLAY_NAME, TEST_PASSWORD);
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

        private String createReservation(String roomId, LocalDateTime startTime, LocalDateTime endTime, String userName)
                        throws Exception {
                CreateReservationRequest request = new CreateReservationRequest(
                                roomId,
                                startTime,
                                endTime,
                                userName);

                MvcResult result = mockMvc.perform(post("/api/reservations")
                                .header("Authorization", "Bearer " + authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andReturn();

                String responseBody = result.getResponse().getContentAsString();
                return objectMapper.readTree(responseBody).get("id").asText();
        }

        @Nested
        @DisplayName("GET /api/reservations/{roomId} - Get reservations by room")
        class GetReservationsByRoom {

                @Test
                @DisplayName("Should retrieve empty list for room with no reservations")
                void testGetReservationsByRoomEmpty() throws Exception {
                        mockMvc.perform(get("/api/reservations/{roomId}", ROOM_ID)
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$", instanceOf(java.util.List.class)))
                                        .andExpect(jsonPath("$.length()", equalTo(0)));
                }

                @Test
                @DisplayName("Should retrieve all reservations for a room")
                void testGetReservationsByRoom() throws Exception {
                        // Create multiple reservations
                        createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);
                        createReservation(ROOM_ID, twoHoursLater.plusHours(1), twoHoursLater.plusHours(3),
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(get("/api/reservations/{roomId}", ROOM_ID)
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.length()", equalTo(2)))
                                        .andExpect(jsonPath("$[0].roomId", equalTo(ROOM_ID)))
                                        .andExpect(jsonPath("$[1].roomId", equalTo(ROOM_ID)));
                }

                @Test
                @DisplayName("Should return empty list for room with no reservations")
                void testGetReservationsByRoomNoReservations() throws Exception {
                        mockMvc.perform(get("/api/reservations/{roomId}", "non-existent-room")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.length()", equalTo(0)));
                }
        }

        @Nested
        @DisplayName("POST /api/reservations - Create reservation")
        class CreateReservation {

                @Test
                @DisplayName("Should create a reservation successfully")
                void testCreateReservationSuccess() throws Exception {
                        CreateReservationRequest request = new CreateReservationRequest(
                                        ROOM_ID,
                                        now,
                                        twoHoursLater,
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(post("/api/reservations")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id", notNullValue()))
                                        .andExpect(jsonPath("$.roomId", equalTo(ROOM_ID)))
                                        .andExpect(jsonPath("$.user", equalTo(TEST_DISPLAY_NAME)))
                                        .andExpect(jsonPath("$.startTime", notNullValue()))
                                        .andExpect(jsonPath("$.endTime", notNullValue()));
                }

                @Test
                @DisplayName("Should return 400 for invalid reservation time (past)")
                void testCreateReservationInvalidTimePast() throws Exception {
                        CreateReservationRequest request = new CreateReservationRequest(
                                        ROOM_ID,
                                        LocalDateTime.now().minusHours(2),
                                        LocalDateTime.now().minusHours(1),
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(post("/api/reservations")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.error", containsString("Virheellinen aika")));
                }

                @Test
                @DisplayName("Should return 400 when end time is before start time")
                void testCreateReservationInvalidTimeOrder() throws Exception {
                        CreateReservationRequest request = new CreateReservationRequest(
                                        ROOM_ID,
                                        twoHoursLater,
                                        now,
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(post("/api/reservations")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Should return 409 when room is already booked")
                void testCreateReservationRoomAlreadyBooked() throws Exception {
                        CreateReservationRequest request1 = new CreateReservationRequest(
                                        ROOM_ID,
                                        now,
                                        twoHoursLater,
                                        TEST_DISPLAY_NAME);

                        // Create first reservation
                        mockMvc.perform(post("/api/reservations")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request1)))
                                        .andExpect(status().isCreated());

                        // Try overlapping reservation
                        CreateReservationRequest request2 = new CreateReservationRequest(
                                        ROOM_ID,
                                        now.plusMinutes(30),
                                        twoHoursLater.plusHours(1),
                                        "Another User");

                        mockMvc.perform(post("/api/reservations")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request2)))
                                        .andExpect(status().isConflict())
                                        .andExpect(jsonPath("$.error", containsString("Huone varattu")));
                }

                @Test
                @DisplayName("Should allow consecutive non-overlapping reservations")
                void testCreateReservationConsecutiveSuccess() throws Exception {
                        // First user creates a reservation
                        CreateReservationRequest request1 = new CreateReservationRequest(
                                        ROOM_ID,
                                        now,
                                        twoHoursLater,
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(post("/api/reservations")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request1)))
                                        .andExpect(status().isCreated());

                        // Register and login as second user
                        String secondUserEmail = "another@example.com";
                        String secondUserToken = registerAndLoginAsUser(secondUserEmail, "Another User", "password456");

                        // Second user creates consecutive reservation (no overlap, same room)
                        CreateReservationRequest request2 = new CreateReservationRequest(
                                        ROOM_ID,
                                        twoHoursLater,
                                        twoHoursLater.plusHours(2),
                                        "Another User");

                        mockMvc.perform(post("/api/reservations")
                                        .header("Authorization", "Bearer " + secondUserToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request2)))
                                        .andExpect(status().isCreated());
                }

                private String registerAndLoginAsUser(String email, String displayName, String password)
                                throws Exception {
                        RegisterRequest registerRequest = new RegisterRequest();
                        registerRequest.setEmail(email);
                        registerRequest.setDisplayName(displayName);
                        registerRequest.setPassword(password);

                        // Register new user
                        mockMvc.perform(post("/api/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(registerRequest)))
                                        .andExpect(status().isCreated());

                        // Login and get token
                        LoginRequest loginRequest = new LoginRequest();
                        loginRequest.setEmail(email);
                        loginRequest.setPassword(password);

                        MvcResult result = mockMvc.perform(post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(loginRequest)))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        String response = result.getResponse().getContentAsString();
                        return objectMapper.readTree(response).get("token").asText();
                }

                @Test
                @DisplayName("Should return 403 without authentication token")
                void testCreateReservationWithoutAuth() throws Exception {
                        CreateReservationRequest request = new CreateReservationRequest(
                                        ROOM_ID,
                                        now,
                                        twoHoursLater,
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(post("/api/reservations")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isForbidden());
                }

                @Test
                @DisplayName("Should return 400 for missing required fields")
                void testCreateReservationMissingFields() throws Exception {
                        String invalidRequest = "{\"roomId\": \"room-1\"}";

                        mockMvc.perform(post("/api/reservations")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(invalidRequest))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("GET /api/reservations/detail/{id} - Get reservation by ID")
        class GetReservationById {

                @Test
                @DisplayName("Should retrieve a reservation by ID")
                void testGetReservationById() throws Exception {
                        String reservationId = createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

                        mockMvc.perform(get("/api/reservations/detail/{id}", reservationId)
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id", equalTo(reservationId)))
                                        .andExpect(jsonPath("$.roomId", equalTo(ROOM_ID)))
                                        .andExpect(jsonPath("$.user", equalTo(TEST_DISPLAY_NAME)));
                }

                @Test
                @DisplayName("Should return 400 for non-existent reservation")
                void testGetReservationByIdNotFound() throws Exception {
                        mockMvc.perform(get("/api/reservations/detail/{id}", "non-existent-id")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("PUT /api/reservations/{id} - Update reservation")
        class UpdateReservation {

                @Test
                @DisplayName("Should update a reservation successfully")
                void testUpdateReservationSuccess() throws Exception {
                        String reservationId = createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

                        LocalDateTime newStartTime = now.plusHours(3);
                        LocalDateTime newEndTime = newStartTime.plusHours(2);

                        CreateReservationRequest updateRequest = new CreateReservationRequest(
                                        ROOM_ID,
                                        newStartTime,
                                        newEndTime,
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(put("/api/reservations/{id}", reservationId)
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id", equalTo(reservationId)))
                                        .andExpect(jsonPath("$.roomId", equalTo(ROOM_ID)));
                }

                @Test
                @DisplayName("Should return 400 when updating non-existent reservation")
                void testUpdateReservationNotFound() throws Exception {
                        CreateReservationRequest updateRequest = new CreateReservationRequest(
                                        ROOM_ID,
                                        now,
                                        twoHoursLater,
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(put("/api/reservations/{id}", "non-existent-id")
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Should return 409 when update causes room conflict")
                void testUpdateReservationConflict() throws Exception {
                        String reservationId1 = createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);
                        createReservation(ROOM_ID, twoHoursLater.plusHours(1),
                                        twoHoursLater.plusHours(3), "Another User");

                        // Try to update first reservation to overlap with second
                        CreateReservationRequest conflictRequest = new CreateReservationRequest(
                                        ROOM_ID,
                                        twoHoursLater.plusMinutes(30),
                                        twoHoursLater.plusHours(2),
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(put("/api/reservations/{id}", reservationId1)
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(conflictRequest)))
                                        .andExpect(status().isConflict());
                }

                @Test
                @DisplayName("Should return 400 for invalid update data")
                void testUpdateReservationInvalidData() throws Exception {
                        String reservationId = createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

                        CreateReservationRequest invalidRequest = new CreateReservationRequest(
                                        ROOM_ID,
                                        LocalDateTime.now().minusHours(1),
                                        LocalDateTime.now(),
                                        TEST_DISPLAY_NAME);

                        mockMvc.perform(put("/api/reservations/{id}", reservationId)
                                        .header("Authorization", "Bearer " + authToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRequest)))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("DELETE /api/reservations/{id} - Delete reservation")
        class DeleteReservation {

                @Test
                @DisplayName("Should delete a reservation successfully")
                void testDeleteReservationSuccess() throws Exception {
                        String reservationId = createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

                        mockMvc.perform(delete("/api/reservations/{id}", reservationId)
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isNoContent());

                        // Verify it's deleted by checking it returns 400
                        mockMvc.perform(get("/api/reservations/detail/{id}", reservationId)
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Should return 400 when deleting non-existent reservation")
                void testDeleteReservationNotFound() throws Exception {
                        mockMvc.perform(delete("/api/reservations/{id}", "non-existent-id")
                                        .header("Authorization", "Bearer " + authToken))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Should return 403 without authentication token")
                void testDeleteReservationWithoutAuth() throws Exception {
                        String reservationId = createReservation(ROOM_ID, now, twoHoursLater, TEST_DISPLAY_NAME);

                        mockMvc.perform(delete("/api/reservations/{id}", reservationId))
                                        .andExpect(status().isForbidden());
                }
        }
}