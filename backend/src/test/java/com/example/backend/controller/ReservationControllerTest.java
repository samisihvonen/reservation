package com.example.backend.controller;

import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService service;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should handle create request")
    void testCreate() throws Exception {
        // Arrange
        // TODO: Mock service calls using service

        // Act
        ResultActions result = mockMvc.perform(post("/create")
                .content(objectMapper.writeValueAsString(new CreateReservationRequest()))
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle update request")
    void testUpdate() throws Exception {
        // Arrange
        // TODO: Mock service calls using service

        // Act
        ResultActions result = mockMvc.perform(put("/update/{id}")
                .content("test-body")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle delete request")
    void testDelete() throws Exception {
        // Arrange
        // TODO: Mock service calls using service

        // Act
        ResultActions result = mockMvc.perform(delete("/delete/{id}")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle get_by_id request")
    void testGetById() throws Exception {
        // Arrange
        // TODO: Mock service calls using service

        // Act
        ResultActions result = mockMvc.perform(get("/{id}/{id}")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
    }
}
