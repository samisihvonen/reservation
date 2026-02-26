package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    @DisplayName("getUserById should return user when user exists")
    void getUserById_WhenUserExists_ReturnsUser() throws Exception {
        // Arrange
        Long userId = 1L;
        UserResponse expectedResponse = new UserResponse(userId, "test@example.com", "Test User", "ADMIN");
        when(adminService.getUserById(userId)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(adminService).getUserById(userId);
    }

    @Test
    @DisplayName("getUserById should return not found when user does not exist")
    void getUserById_WhenUserDoesNotExist_ThrowsException() throws Exception {
        // Arrange
        Long userId = 999L;
        when(adminService.getUserById(userId)).thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isInternalServerError());

        verify(adminService).getUserById(userId);
    }

    @Test
    @DisplayName("updateUser should update and return user when request is valid")
    void updateUser_WhenRequestIsValid_ReturnsUpdatedUser() throws Exception {
        // Arrange
        Long userId = 1L;
        UserRequest request = new UserRequest("updated@example.com", "Updated User", "USER");
        UserResponse expectedResponse = new UserResponse(userId, "updated@example.com", "Updated User", "USER");
        when(adminService.updateUser(eq(userId), any(UserRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType("application/json")
                        .content("{\"email\":\"updated@example.com\",\"name\":\"Updated User\",\"role\":\"USER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(adminService).updateUser(eq(userId), any(UserRequest.class));
    }

    @Test
    @DisplayName("updateUser should return bad request when request is invalid")
    void updateUser_WhenRequestIsInvalid_ReturnsBadRequest() throws Exception {
        // Arrange
        Long userId = 1L;

        // Act & Assert
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType("application/json")
                        .content("{\"email\":\"\",\"name\":\"\",\"role\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(adminService).updateUser(eq(userId), any(UserRequest.class));
    }

    @Test
    @DisplayName("deleteUser should delete user and return no content when user exists")
    void deleteUser_WhenUserExists_ReturnsNoContent() throws Exception {
        // Arrange
        Long userId = 1L;
        doNothing().when(adminService).deleteUser(userId);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(adminService).deleteUser(userId);
    }

    @Test
    @DisplayName("deleteUser should return not found when user does not exist")
    void deleteUser_WhenUserDoesNotExist_ThrowsException() throws Exception {
        // Arrange
        Long userId = 999L;
        doNothing().when(adminService).deleteUser(userId);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(adminService).deleteUser(userId);
    }

    @Test
    @DisplayName("changeUserEmail should update email and return user when request is valid")
    void changeUserEmail_WhenRequestIsValid_ReturnsUpdatedUser() throws Exception {
        // Arrange
        Long userId = 1L;
        String newEmail = "new@example.com";
        EmailChangeRequest request = new EmailChangeRequest(newEmail);
        UserResponse expectedResponse = new UserResponse(userId, newEmail, "Test User", "ADMIN");
        when(adminService.changeUserEmail(eq(userId), eq(newEmail))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(patch("/users/{id}/email", userId)
                        .contentType("application/json")
                        .content("{\"newEmail\":\"new@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(adminService).changeUserEmail(eq(userId), eq(newEmail));
    }

    @Test
    @DisplayName("createRoom should create and return room when request is valid")
    void createRoom_WhenRequestIsValid_ReturnsCreatedRoom() throws Exception {
        // Arrange
        RoomRequest request = new RoomRequest("Conference Room", 10);
        RoomResponse expectedResponse = new RoomResponse("1", "Conference Room", 10);
        when(adminService.createRoom(any(RoomRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/rooms")
                        .contentType("application/json")
                        .content("{\"name\":\"Conference Room\",\"capacity\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.capacity").value(10));

        verify(adminService).createRoom(any(RoomRequest.class));
    }

    @Test
    @DisplayName("createRoom should return bad request when request is invalid")
    void createRoom_WhenRequestIsInvalid_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/rooms")
                        .contentType("application/json")
                        .content("{\"name\":\"\",\"capacity\":-1}"))
                .andExpect(status().isBadRequest());

        verify(adminService).createRoom(any(RoomRequest.class));
    }

    @Test
    @DisplayName("updateRoom should update and return room when request is valid")
    void updateRoom_WhenRequestIsValid_ReturnsUpdatedRoom() throws Exception {
        // Arrange
        String roomId = "1";
        RoomRequest request = new RoomRequest("Updated Room", 15);
        RoomResponse expectedResponse = new RoomResponse(roomId, "Updated Room", 15);
        when(adminService.updateRoom(eq(roomId), any(RoomRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(put("/rooms/{roomId}", roomId)
                        .contentType("application/json")
                        .content("{\"name\":\"Updated Room\",\"capacity\":15}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.name").value("Updated Room"))
                .andExpect(jsonPath("$.capacity").value(15));

        verify(adminService).updateRoom(eq(roomId), any(RoomRequest.class));
    }

    @Test
    @DisplayName("updateRoom should return bad request when request is invalid")
    void updateRoom_WhenRequestIsInvalid_ReturnsBadRequest() throws Exception {
        // Arrange
        String roomId = "1";

        // Act & Assert
        mockMvc.perform(put("/rooms/{roomId}", roomId)
                        .contentType("application/json")
                        .content("{\"name\":\"\",\"capacity\":-1}"))
                .andExpect(status().isBadRequest());

        verify(adminService).updateRoom(eq(roomId), any(RoomRequest.class));
    }

    @Test
    @DisplayName("deleteRoom should delete room and return no content when room exists")
    void deleteRoom_WhenRoomExists_ReturnsNoContent() throws Exception {
        // Arrange
        String roomId = "1";
        doNothing().when(adminService).deleteRoom(roomId);

        // Act & Assert
        mockMvc.perform(delete("/rooms/{roomId}", roomId))
                .andExpect(status().isNoContent());

        verify(adminService).deleteRoom(roomId);
    }

    @Test
    @DisplayName("changeRoomName should update name and return room when request is valid")
    void changeRoomName_WhenRequestIsValid_ReturnsUpdatedRoom() throws Exception {
        // Arrange
        String roomId = "1";
        String newName = "New Room Name";
        RoomNameChangeRequest request = new RoomNameChangeRequest(newName);
        RoomResponse expectedResponse = new RoomResponse(roomId, newName, 10);
        when(adminService.changeRoomName(eq(roomId), eq(newName))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(patch("/rooms/{roomId}/name", roomId)
                        .contentType("application/json")
                        .content("{\"newName\":\"New Room Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.capacity").value(10));

        verify(adminService).changeRoomName(eq(roomId), eq(newName));
    }

    @Test
    @DisplayName("changeRoomCapacity should update capacity and return room when request is valid")
    void changeRoomCapacity_WhenRequestIsValid_ReturnsUpdatedRoom() throws Exception {
        // Arrange
        String roomId = "1";
        int newCapacity = 20;
        RoomCapacityChangeRequest request = new RoomCapacityChangeRequest(newCapacity);
        RoomResponse expectedResponse = new RoomResponse(roomId, "Conference Room", newCapacity);
        when(adminService.changeRoomCapacity(eq(roomId), eq(newCapacity))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(patch("/rooms/{roomId}/capacity", roomId)
                        .contentType("application/json")
                        .content("{\"newCapacity\":20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.capacity").value(newCapacity));

        verify(adminService).changeRoomCapacity(eq(roomId), eq(newCapacity));
    }
}