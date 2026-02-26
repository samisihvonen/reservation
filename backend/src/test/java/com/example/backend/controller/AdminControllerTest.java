package com.example.backend.controller;

import com.example.backend.dto.request.EmailChangeRequest;
import com.example.backend.dto.request.RoomCapacityChangeRequest;
import com.example.backend.dto.request.RoomNameChangeRequest;
import com.example.backend.dto.request.RoomRequest;
import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.RoomResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private UserRequest userRequest;
    private RoomResponse roomResponse;
    private RoomRequest roomRequest;
    private EmailChangeRequest emailChangeRequest;
    private RoomNameChangeRequest roomNameChangeRequest;
    private RoomCapacityChangeRequest roomCapacityChangeRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userResponse = new UserResponse(1L, "test@example.com", "John Doe", "ADMIN");
        userRequest = new UserRequest("new@example.com", "John Updated", "ADMIN");
        roomResponse = new RoomResponse("ROOM1", "Conference Room 1", 10);
        roomRequest = new RoomRequest("Conference Room 1", 10);
        emailChangeRequest = new EmailChangeRequest("new.email@example.com");
        roomNameChangeRequest = new RoomNameChangeRequest("New Room Name");
        roomCapacityChangeRequest = new RoomCapacityChangeRequest(20);
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenUserExists() throws Exception {
        given(adminService.getUserById(anyLong())).willReturn(ResponseEntity.ok(userResponse));

        mockMvc.perform(get("/api/admin/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUserResponse_whenUpdateIsSuccessful() throws Exception {
        given(adminService.updateUser(anyLong(), any(UserRequest.class)))
                .willReturn(ResponseEntity.ok(userResponse));

        mockMvc.perform(put("/api/admin/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void deleteUser_shouldReturnNoContent_whenDeletionIsSuccessful() throws Exception {
        willDoNothing().given(adminService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/admin/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeUserEmail_shouldReturnUpdatedUserResponse_whenEmailChangeIsSuccessful() throws Exception {
        given(adminService.changeUserEmail(anyLong(), any(EmailChangeRequest.class)))
                .willReturn(ResponseEntity.ok(userResponse));

        mockMvc.perform(patch("/api/admin/users/{id}/email", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createRoom_shouldReturnRoomResponse_whenRoomCreationIsSuccessful() throws Exception {
        given(adminService.createRoom(any(RoomRequest.class)))
                .willReturn(ResponseEntity.ok(roomResponse));

        mockMvc.perform(post("/api/admin/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Conference Room 1"));
    }

    @Test
    void updateRoom_shouldReturnUpdatedRoomResponse_whenUpdateIsSuccessful() throws Exception {
        given(adminService.updateRoom(anyString(), any(RoomRequest.class)))
                .willReturn(ResponseEntity.ok(roomResponse));

        mockMvc.perform(put("/api/admin/rooms/{roomId}", "ROOM1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Conference Room 1"));
    }

    @Test
    void deleteRoom_shouldReturnNoContent_whenDeletionIsSuccessful() throws Exception {
        willDoNothing().given(adminService).deleteRoom(anyString());

        mockMvc.perform(delete("/api/admin/rooms/{roomId}", "ROOM1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeRoomName_shouldReturnUpdatedRoomResponse_whenNameChangeIsSuccessful() throws Exception {
        given(adminService.changeRoomName(anyString(), any(RoomNameChangeRequest.class)))
                .willReturn(ResponseEntity.ok(roomResponse));

        mockMvc.perform(patch("/api/admin/rooms/{roomId}/name", "ROOM1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomNameChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Conference Room 1"));
    }

    @Test
    void changeRoomCapacity_shouldReturnUpdatedRoomResponse_whenCapacityChangeIsSuccessful() throws Exception {
        RoomResponse updatedRoomResponse = new RoomResponse("ROOM1", "Conference Room 1", 20);
        given(adminService.changeRoomCapacity(anyString(), any(RoomCapacityChangeRequest.class)))
                .willReturn(ResponseEntity.ok(updatedRoomResponse));

        mockMvc.perform(patch("/api/admin/rooms/{roomId}/capacity", "ROOM1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomCapacityChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(20));
    }
}