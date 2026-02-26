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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @DisplayName("Should return user when getUserById is called with valid id")
    void shouldReturnUserWhenGetUserByIdIsCalledWithValidId() throws Exception {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");

        when(adminService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return 404 when getUserById is called with non-existent id")
    void shouldReturn404WhenGetUserByIdIsCalledWithNonExistentId() throws Exception {
        when(adminService.getUserById(999L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update user and return updated user when updateUser is called with valid data")
    void shouldUpdateUserAndReturnUpdatedUserWhenUpdateUserIsCalledWithValidData() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("updated@example.com");
        userRequest.setName("Updated Name");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("updated@example.com");
        userResponse.setName("Updated Name");

        when(adminService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("Should return 400 when updateUser is called with invalid data")
    void shouldReturn400WhenUpdateUserIsCalledWithInvalidData() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("invalid-email");
        userRequest.setName("");

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 204 when deleteUser is called with valid id")
    void shouldReturn204WhenDeleteUserIsCalledWithValidId() throws Exception {
        doNothing().when(adminService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 when deleteUser is called with non-existent id")
    void shouldReturn404WhenDeleteUserIsCalledWithNonExistentId() throws Exception {
        doThrow(new RuntimeException("User not found")).when(adminService).deleteUser(999L);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should change user email and return updated user when changeUserEmail is called with valid data")
    void shouldChangeUserEmailAndReturnUpdatedUserWhenChangeUserEmailIsCalledWithValidData() throws Exception {
        EmailChangeRequest emailChangeRequest = new EmailChangeRequest();
        emailChangeRequest.setNewEmail("new@example.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("new@example.com");

        when(adminService.changeUserEmail(eq(1L), anyString())).thenReturn(userResponse);

        mockMvc.perform(patch("/users/1/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @DisplayName("Should return 400 when changeUserEmail is called with invalid email")
    void shouldReturn400WhenChangeUserEmailIsCalledWithInvalidEmail() throws Exception {
        EmailChangeRequest emailChangeRequest = new EmailChangeRequest();
        emailChangeRequest.setNewEmail("invalid-email");

        mockMvc.perform(patch("/users/1/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailChangeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should create room and return created room when createRoom is called with valid data")
    void shouldCreateRoomAndReturnCreatedRoomWhenCreateRoomIsCalledWithValidData() throws Exception {
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setName("Conference Room");
        roomRequest.setCapacity(10);

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId("1");
        roomResponse.setName("Conference Room");
        roomResponse.setCapacity(10);

        when(adminService.createRoom(any(RoomRequest.class))).thenReturn(roomResponse);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    @DisplayName("Should return 400 when createRoom is called with invalid data")
    void shouldReturn400WhenCreateRoomIsCalledWithInvalidData() throws Exception {
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setName("");
        roomRequest.setCapacity(-1);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update room and return updated room when updateRoom is called with valid data")
    void shouldUpdateRoomAndReturnUpdatedRoomWhenUpdateRoomIsCalledWithValidData() throws Exception {
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setName("Updated Room");
        roomRequest.setCapacity(15);

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId("1");
        roomResponse.setName("Updated Room");
        roomResponse.setCapacity(15);

        when(adminService.updateRoom(eq("1"), any(RoomRequest.class))).thenReturn(roomResponse);

        mockMvc.perform(put("/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Updated Room"))
                .andExpect(jsonPath("$.capacity").value(15));
    }

    @Test
    @DisplayName("Should return 400 when updateRoom is called with invalid data")
    void shouldReturn400WhenUpdateRoomIsCalledWithInvalidData() throws Exception {
        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setName("");
        roomRequest.setCapacity(-1);

        mockMvc.perform(put("/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 204 when deleteRoom is called with valid id")
    void shouldReturn204WhenDeleteRoomIsCalledWithValidId() throws Exception {
        doNothing().when(adminService).deleteRoom("1");

        mockMvc.perform(delete("/rooms/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 when deleteRoom is called with non-existent id")
    void shouldReturn404WhenDeleteRoomIsCalledWithNonExistentId() throws Exception {
        doThrow(new RuntimeException("Room not found")).when(adminService).deleteRoom("999");

        mockMvc.perform(delete("/rooms/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should change room name and return updated room when changeRoomName is called with valid data")
    void shouldChangeRoomNameAndReturnUpdatedRoomWhenChangeRoomNameIsCalledWithValidData() throws Exception {
        RoomNameChangeRequest roomNameChangeRequest = new RoomNameChangeRequest();
        roomNameChangeRequest.setNewName("New Room Name");

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId("1");
        roomResponse.setName("New Room Name");
        roomResponse.setCapacity(10);

        when(adminService.changeRoomName(eq("1"), anyString())).thenReturn(roomResponse);

        mockMvc.perform(patch("/rooms/1/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomNameChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("New Room Name"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    @DisplayName("Should return 400 when changeRoomName is called with empty name")
    void shouldReturn400WhenChangeRoomNameIsCalledWithEmptyName() throws Exception {
        RoomNameChangeRequest roomNameChangeRequest = new RoomNameChangeRequest();
        roomNameChangeRequest.setNewName("");

        mockMvc.perform(patch("/rooms/1/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomNameChangeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should change room capacity and return updated room when changeRoomCapacity is called with valid data")
    void shouldChangeRoomCapacityAndReturnUpdatedRoomWhenChangeRoomCapacityIsCalledWithValidData() throws Exception {
        RoomCapacityChangeRequest roomCapacityChangeRequest = new RoomCapacityChangeRequest();
        roomCapacityChangeRequest.setNewCapacity(20);

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId("1");
        roomResponse.setName("Conference Room");
        roomResponse.setCapacity(20);

        when(adminService.changeRoomCapacity(eq("1"), anyInt())).thenReturn(roomResponse);

        mockMvc.perform(patch("/rooms/1/capacity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomCapacityChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.capacity").value(20));
    }

    @Test
    @DisplayName("Should return 400 when changeRoomCapacity is called with invalid capacity")
    void shouldReturn400WhenChangeRoomCapacityIsCalledWithInvalidCapacity() throws Exception {
        RoomCapacityChangeRequest roomCapacityChangeRequest = new RoomCapacityChangeRequest();
        roomCapacityChangeRequest.setNewCapacity(-1);

        mockMvc.perform(patch("/rooms/1/capacity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomCapacityChangeRequest)))
                .andExpect(status().isBadRequest());
    }
}