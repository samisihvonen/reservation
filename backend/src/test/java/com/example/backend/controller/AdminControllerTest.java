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

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private UserResponse mockUserResponse;
    private UserRequest mockUserRequest;
    private RoomResponse mockRoomResponse;
    private RoomRequest mockRoomRequest;
    private EmailChangeRequest mockEmailChangeRequest;
    private RoomNameChangeRequest mockRoomNameChangeRequest;
    private RoomCapacityChangeRequest mockRoomCapacityChangeRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUserResponse = new UserResponse(1L, "test@example.com", "Test User");
        mockUserRequest = new UserRequest("test@example.com", "Test User");
        mockRoomResponse = new RoomResponse("ROOM1", "Test Room", 10);
        mockRoomRequest = new RoomRequest("Test Room", 10);
        mockEmailChangeRequest = new EmailChangeRequest("new@example.com");
        mockRoomNameChangeRequest = new RoomNameChangeRequest("New Room Name");
        mockRoomCapacityChangeRequest = new RoomCapacityChangeRequest(20);
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenUserExists() throws Exception {
        given(adminService.getUserById(anyLong())).willReturn(ResponseEntity.ok(mockUserResponse));

        mockMvc.perform(get("/api/admin/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUserResponse_whenUpdateIsSuccessful() throws Exception {
        given(adminService.updateUser(anyLong(), any(UserRequest.class)))
                .willReturn(ResponseEntity.ok(mockUserResponse));

        mockMvc.perform(put("/api/admin/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
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
        UserResponse updatedUserResponse = new UserResponse(1L, "new@example.com", "Test User");
        given(adminService.changeUserEmail(anyLong(), any(EmailChangeRequest.class)))
                .willReturn(ResponseEntity.ok(updatedUserResponse));

        mockMvc.perform(patch("/api/admin/users/{id}/email", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockEmailChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void createRoom_shouldReturnRoomResponse_whenCreationIsSuccessful() throws Exception {
        given(adminService.createRoom(any(RoomRequest.class)))
                .willReturn(ResponseEntity.ok(mockRoomResponse));

        mockMvc.perform(post("/api/admin/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRoomRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Room"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    void updateRoom_shouldReturnUpdatedRoomResponse_whenUpdateIsSuccessful() throws Exception {
        given(adminService.updateRoom(anyString(), any(RoomRequest.class)))
                .willReturn(ResponseEntity.ok(mockRoomResponse));

        mockMvc.perform(put("/api/admin/rooms/{roomId}", "ROOM1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRoomRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Room"))
                .andExpect(jsonPath("$.capacity").value(10));
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
        RoomResponse updatedRoomResponse = new RoomResponse("ROOM1", "New Room Name", 10);
        given(adminService.changeRoomName(anyString(), any(RoomNameChangeRequest.class)))
                .willReturn(ResponseEntity.ok(updatedRoomResponse));

        mockMvc.perform(patch("/api/admin/rooms/{roomId}/name", "ROOM1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRoomNameChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Room Name"));
    }

    @Test
    void changeRoomCapacity_shouldReturnUpdatedRoomResponse_whenCapacityChangeIsSuccessful() throws Exception {
        RoomResponse updatedRoomResponse = new RoomResponse("ROOM1", "Test Room", 20);
        given(adminService.changeRoomCapacity(anyString(), any(RoomCapacityChangeRequest.class)))
                .willReturn(ResponseEntity.ok(updatedRoomResponse));

        mockMvc.perform(patch("/api/admin/rooms/{roomId}/capacity", "ROOM1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRoomCapacityChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(20));
    }
}