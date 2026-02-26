package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("getUserById should return user when user exists")
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "test@example.com", "Test User", "ADMIN");
        when(adminService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("getUserById should return not found when user does not exist")
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(adminService.getUserById(1L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("updateUser should return updated user when update is successful")
    void updateUser_ShouldReturnUpdatedUser_WhenUpdateIsSuccessful() throws Exception {
        UserRequest userRequest = new UserRequest("updated@example.com", "Updated User", "ADMIN");
        UserResponse userResponse = new UserResponse(1L, "updated@example.com", "Updated User", "ADMIN");

        when(adminService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("updateUser should return bad request when request is invalid")
    void updateUser_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        UserRequest invalidUserRequest = new UserRequest("", "", "");

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deleteUser should return no content when deletion is successful")
    void deleteUser_ShouldReturnNoContent_WhenDeletionIsSuccessful() throws Exception {
        doNothing().when(adminService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deleteUser should return not found when user does not exist")
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        doThrow(new RuntimeException("User not found")).when(adminService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("changeUserEmail should return updated user when email change is successful")
    void changeUserEmail_ShouldReturnUpdatedUser_WhenEmailChangeIsSuccessful() throws Exception {
        EmailChangeRequest emailChangeRequest = new EmailChangeRequest("new@example.com");
        UserResponse userResponse = new UserResponse(1L, "new@example.com", "Test User", "ADMIN");

        when(adminService.changeUserEmail(eq(1L), anyString())).thenReturn(userResponse);

        mockMvc.perform(patch("/users/{id}/email", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("createRoom should return created room when creation is successful")
    void createRoom_ShouldReturnCreatedRoom_WhenCreationIsSuccessful() throws Exception {
        RoomRequest roomRequest = new RoomRequest("Conference Room", 10);
        RoomResponse roomResponse = new RoomResponse("1", "Conference Room", 10);

        when(adminService.createRoom(any(RoomRequest.class))).thenReturn(roomResponse);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    @DisplayName("createRoom should return bad request when request is invalid")
    void createRoom_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        RoomRequest invalidRoomRequest = new RoomRequest("", -1);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoomRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateRoom should return updated room when update is successful")
    void updateRoom_ShouldReturnUpdatedRoom_WhenUpdateIsSuccessful() throws Exception {
        RoomRequest roomRequest = new RoomRequest("Updated Room", 15);
        RoomResponse roomResponse = new RoomResponse("1", "Updated Room", 15);

        when(adminService.updateRoom(eq("1"), any(RoomRequest.class))).thenReturn(roomResponse);

        mockMvc.perform(put("/rooms/{roomId}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Updated Room"))
                .andExpect(jsonPath("$.capacity").value(15));
    }

    @Test
    @DisplayName("updateRoom should return bad request when request is invalid")
    void updateRoom_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        RoomRequest invalidRoomRequest = new RoomRequest("", -1);

        mockMvc.perform(put("/rooms/{roomId}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoomRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deleteRoom should return no content when deletion is successful")
    void deleteRoom_ShouldReturnNoContent_WhenDeletionIsSuccessful() throws Exception {
        doNothing().when(adminService).deleteRoom("1");

        mockMvc.perform(delete("/rooms/{roomId}", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deleteRoom should return not found when room does not exist")
    void deleteRoom_ShouldReturnNotFound_WhenRoomDoesNotExist() throws Exception {
        doThrow(new RuntimeException("Room not found")).when(adminService).deleteRoom("1");

        mockMvc.perform(delete("/rooms/{roomId}", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("changeRoomName should return updated room when name change is successful")
    void changeRoomName_ShouldReturnUpdatedRoom_WhenNameChangeIsSuccessful() throws Exception {
        RoomNameChangeRequest roomNameChangeRequest = new RoomNameChangeRequest("New Room Name");
        RoomResponse roomResponse = new RoomResponse("1", "New Room Name", 10);

        when(adminService.changeRoomName(eq("1"), anyString())).thenReturn(roomResponse);

        mockMvc.perform(patch("/rooms/{roomId}/name", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomNameChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("New Room Name"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    @DisplayName("changeRoomCapacity should return updated room when capacity change is successful")
    void changeRoomCapacity_ShouldReturnUpdatedRoom_WhenCapacityChangeIsSuccessful() throws Exception {
        RoomCapacityChangeRequest roomCapacityChangeRequest = new RoomCapacityChangeRequest(20);
        RoomResponse roomResponse = new RoomResponse("1", "Conference Room", 20);

        when(adminService.changeRoomCapacity(eq("1"), anyInt())).thenReturn(roomResponse);

        mockMvc.perform(patch("/rooms/{roomId}/capacity", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomCapacityChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.capacity").value(20));
    }
}