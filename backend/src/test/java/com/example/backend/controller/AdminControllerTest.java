package com.example.backend.controller;

import com.example.backend.dto.request.EmailChangeRequest;
import com.example.backend.dto.request.RoomCapacityChangeRequest;
import com.example.backend.dto.request.RoomNameChangeRequest;
import com.example.backend.dto.request.RoomRequest;
import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.RoomResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private UserResponse userResponse;
    private UserRequest userRequest;
    private RoomResponse roomResponse;
    private RoomRequest roomRequest;
    private EmailChangeRequest emailChangeRequest;
    private RoomNameChangeRequest roomNameChangeRequest;
    private RoomCapacityChangeRequest roomCapacityChangeRequest;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(1L, "test@example.com", "Test User");
        userRequest = new UserRequest("new@example.com", "New User");
        roomResponse = new RoomResponse("ROOM1", "Test Room", 10);
        roomRequest = new RoomRequest("New Room", 20);
        emailChangeRequest = new EmailChangeRequest("updated@example.com");
        roomNameChangeRequest = new RoomNameChangeRequest("Updated Room Name");
        roomCapacityChangeRequest = new RoomCapacityChangeRequest(30);
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() {
        when(adminService.getUserById(anyLong())).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = adminController.getUserById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(adminService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() {
        when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<UserResponse> response = adminController.getUserById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(adminService, times(1)).getUserById(1L);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserResponse_WhenUpdateIsSuccessful() {
        when(adminService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = adminController.updateUser(1L, userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(adminService, times(1)).updateUser(1L, userRequest);
    }

    @Test
    void updateUser_ShouldReturnBadRequest_WhenUpdateFails() {
        when(adminService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(null);

        ResponseEntity<UserResponse> response = adminController.updateUser(1L, userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).updateUser(1L, userRequest);
    }

    @Test
    void deleteUser_ShouldReturnNoContent_WhenDeletionIsSuccessful() {
        when(adminService.deleteUser(anyLong())).thenReturn(true);

        ResponseEntity<Void> response = adminController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenDeletionFails() {
        when(adminService.deleteUser(anyLong())).thenReturn(false);

        ResponseEntity<Void> response = adminController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(adminService, times(1)).deleteUser(1L);
    }

    @Test
    void changeUserEmail_ShouldReturnUpdatedUserResponse_WhenEmailChangeIsSuccessful() {
        when(adminService.changeUserEmail(anyLong(), any(EmailChangeRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = adminController.changeUserEmail(1L, emailChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(adminService, times(1)).changeUserEmail(1L, emailChangeRequest);
    }

    @Test
    void changeUserEmail_ShouldReturnBadRequest_WhenEmailChangeFails() {
        when(adminService.changeUserEmail(anyLong(), any(EmailChangeRequest.class))).thenReturn(null);

        ResponseEntity<UserResponse> response = adminController.changeUserEmail(1L, emailChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).changeUserEmail(1L, emailChangeRequest);
    }

    @Test
    void createRoom_ShouldReturnCreatedRoomResponse_WhenCreationIsSuccessful() {
        when(adminService.createRoom(any(RoomRequest.class))).thenReturn(roomResponse);

        ResponseEntity<RoomResponse> response = adminController.createRoom(roomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(roomResponse, response.getBody());
        verify(adminService, times(1)).createRoom(roomRequest);
    }

    @Test
    void createRoom_ShouldReturnBadRequest_WhenCreationFails() {
        when(adminService.createRoom(any(RoomRequest.class))).thenReturn(null);

        ResponseEntity<RoomResponse> response = adminController.createRoom(roomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).createRoom(roomRequest);
    }

    @Test
    void updateRoom_ShouldReturnUpdatedRoomResponse_WhenUpdateIsSuccessful() {
        when(adminService.updateRoom(anyString(), any(RoomRequest.class))).thenReturn(roomResponse);

        ResponseEntity<RoomResponse> response = adminController.updateRoom("ROOM1", roomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roomResponse, response.getBody());
        verify(adminService, times(1)).updateRoom("ROOM1", roomRequest);
    }

    @Test
    void updateRoom_ShouldReturnBadRequest_WhenUpdateFails() {
        when(adminService.updateRoom(anyString(), any(RoomRequest.class))).thenReturn(null);

        ResponseEntity<RoomResponse> response = adminController.updateRoom("ROOM1", roomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).updateRoom("ROOM1", roomRequest);
    }

    @Test
    void deleteRoom_ShouldReturnNoContent_WhenDeletionIsSuccessful() {
        when(adminService.deleteRoom(anyString())).thenReturn(true);

        ResponseEntity<Void> response = adminController.deleteRoom("ROOM1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService, times(1)).deleteRoom("ROOM1");
    }

    @Test
    void deleteRoom_ShouldReturnNotFound_WhenDeletionFails() {
        when(adminService.deleteRoom(anyString())).thenReturn(false);

        ResponseEntity<Void> response = adminController.deleteRoom("ROOM1");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(adminService, times(1)).deleteRoom("ROOM1");
    }

    @Test
    void changeRoomName_ShouldReturnUpdatedRoomResponse_WhenNameChangeIsSuccessful() {
        when(adminService.changeRoomName(anyString(), any(RoomNameChangeRequest.class))).thenReturn(roomResponse);

        ResponseEntity<RoomResponse> response = adminController.changeRoomName("ROOM1", roomNameChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roomResponse, response.getBody());
        verify(adminService, times(1)).changeRoomName("ROOM1", roomNameChangeRequest);
    }

    @Test
    void changeRoomName_ShouldReturnBadRequest_WhenNameChangeFails() {
        when(adminService.changeRoomName(anyString(), any(RoomNameChangeRequest.class))).thenReturn(null);

        ResponseEntity<RoomResponse> response = adminController.changeRoomName("ROOM1", roomNameChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).changeRoomName("ROOM1", roomNameChangeRequest);
    }

    @Test
    void changeRoomCapacity_ShouldReturnUpdatedRoomResponse_WhenCapacityChangeIsSuccessful() {
        when(adminService.changeRoomCapacity(anyString(), any(RoomCapacityChangeRequest.class))).thenReturn(roomResponse);

        ResponseEntity<RoomResponse> response = adminController.changeRoomCapacity("ROOM1", roomCapacityChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roomResponse, response.getBody());
        verify(adminService, times(1)).changeRoomCapacity("ROOM1", roomCapacityChangeRequest);
    }

    @Test
    void changeRoomCapacity_ShouldReturnBadRequest_WhenCapacityChangeFails() {
        when(adminService.changeRoomCapacity(anyString(), any(RoomCapacityChangeRequest.class))).thenReturn(null);

        ResponseEntity<RoomResponse> response = adminController.changeRoomCapacity("ROOM1", roomCapacityChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).changeRoomCapacity("ROOM1", roomCapacityChangeRequest);
    }
}