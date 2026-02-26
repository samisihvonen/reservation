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

    private UserResponse mockUserResponse;
    private UserRequest mockUserRequest;
    private RoomResponse mockRoomResponse;
    private RoomRequest mockRoomRequest;
    private EmailChangeRequest mockEmailChangeRequest;
    private RoomNameChangeRequest mockRoomNameChangeRequest;
    private RoomCapacityChangeRequest mockRoomCapacityChangeRequest;

    @BeforeEach
    void setUp() {
        mockUserResponse = new UserResponse(1L, "test@example.com", "Test User");
        mockUserRequest = new UserRequest("new@example.com", "New User");
        mockRoomResponse = new RoomResponse("room123", "Test Room", 10);
        mockRoomRequest = new RoomRequest("New Room", 20);
        mockEmailChangeRequest = new EmailChangeRequest("newemail@example.com");
        mockRoomNameChangeRequest = new RoomNameChangeRequest("Updated Room");
        mockRoomCapacityChangeRequest = new RoomCapacityChangeRequest(30);
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() {
        when(adminService.getUserById(anyLong())).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = adminController.getUserById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUserResponse, response.getBody());
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
        when(adminService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = adminController.updateUser(1L, mockUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUserResponse, response.getBody());
        verify(adminService, times(1)).updateUser(eq(1L), any(UserRequest.class));
    }

    @Test
    void updateUser_ShouldReturnBadRequest_WhenUpdateFails() {
        when(adminService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(null);

        ResponseEntity<UserResponse> response = adminController.updateUser(1L, mockUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).updateUser(eq(1L), any(UserRequest.class));
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
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        when(adminService.deleteUser(anyLong())).thenReturn(false);

        ResponseEntity<Void> response = adminController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(adminService, times(1)).deleteUser(1L);
    }

    @Test
    void changeUserEmail_ShouldReturnUpdatedUserResponse_WhenEmailChangeIsSuccessful() {
        when(adminService.changeUserEmail(anyLong(), any(EmailChangeRequest.class))).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = adminController.changeUserEmail(1L, mockEmailChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUserResponse, response.getBody());
        verify(adminService, times(1)).changeUserEmail(eq(1L), any(EmailChangeRequest.class));
    }

    @Test
    void changeUserEmail_ShouldReturnBadRequest_WhenEmailChangeFails() {
        when(adminService.changeUserEmail(anyLong(), any(EmailChangeRequest.class))).thenReturn(null);

        ResponseEntity<UserResponse> response = adminController.changeUserEmail(1L, mockEmailChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).changeUserEmail(eq(1L), any(EmailChangeRequest.class));
    }

    @Test
    void createRoom_ShouldReturnCreatedRoomResponse_WhenCreationIsSuccessful() {
        when(adminService.createRoom(any(RoomRequest.class))).thenReturn(mockRoomResponse);

        ResponseEntity<RoomResponse> response = adminController.createRoom(mockRoomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockRoomResponse, response.getBody());
        verify(adminService, times(1)).createRoom(any(RoomRequest.class));
    }

    @Test
    void createRoom_ShouldReturnBadRequest_WhenCreationFails() {
        when(adminService.createRoom(any(RoomRequest.class))).thenReturn(null);

        ResponseEntity<RoomResponse> response = adminController.createRoom(mockRoomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).createRoom(any(RoomRequest.class));
    }

    @Test
    void updateRoom_ShouldReturnUpdatedRoomResponse_WhenUpdateIsSuccessful() {
        when(adminService.updateRoom(anyString(), any(RoomRequest.class))).thenReturn(mockRoomResponse);

        ResponseEntity<RoomResponse> response = adminController.updateRoom("room123", mockRoomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRoomResponse, response.getBody());
        verify(adminService, times(1)).updateRoom(eq("room123"), any(RoomRequest.class));
    }

    @Test
    void updateRoom_ShouldReturnBadRequest_WhenUpdateFails() {
        when(adminService.updateRoom(anyString(), any(RoomRequest.class))).thenReturn(null);

        ResponseEntity<RoomResponse> response = adminController.updateRoom("room123", mockRoomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).updateRoom(eq("room123"), any(RoomRequest.class));
    }

    @Test
    void deleteRoom_ShouldReturnNoContent_WhenDeletionIsSuccessful() {
        when(adminService.deleteRoom(anyString())).thenReturn(true);

        ResponseEntity<Void> response = adminController.deleteRoom("room123");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService, times(1)).deleteRoom("room123");
    }

    @Test
    void deleteRoom_ShouldReturnNotFound_WhenRoomDoesNotExist() {
        when(adminService.deleteRoom(anyString())).thenReturn(false);

        ResponseEntity<Void> response = adminController.deleteRoom("room123");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(adminService, times(1)).deleteRoom("room123");
    }

    @Test
    void changeRoomName_ShouldReturnUpdatedRoomResponse_WhenNameChangeIsSuccessful() {
        when(adminService.changeRoomName(anyString(), any(RoomNameChangeRequest.class))).thenReturn(mockRoomResponse);

        ResponseEntity<RoomResponse> response = adminController.changeRoomName("room123", mockRoomNameChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRoomResponse, response.getBody());
        verify(adminService, times(1)).changeRoomName(eq("room123"), any(RoomNameChangeRequest.class));
    }

    @Test
    void changeRoomName_ShouldReturnBadRequest_WhenNameChangeFails() {
        when(adminService.changeRoomName(anyString(), any(RoomNameChangeRequest.class))).thenReturn(null);

        ResponseEntity<RoomResponse> response = adminController.changeRoomName("room123", mockRoomNameChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).changeRoomName(eq("room123"), any(RoomNameChangeRequest.class));
    }

    @Test
    void changeRoomCapacity_ShouldReturnUpdatedRoomResponse_WhenCapacityChangeIsSuccessful() {
        when(adminService.changeRoomCapacity(anyString(), any(RoomCapacityChangeRequest.class))).thenReturn(mockRoomResponse);

        ResponseEntity<RoomResponse> response = adminController.changeRoomCapacity("room123", mockRoomCapacityChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRoomResponse, response.getBody());
        verify(adminService, times(1)).changeRoomCapacity(eq("room123"), any(RoomCapacityChangeRequest.class));
    }

    @Test
    void changeRoomCapacity_ShouldReturnBadRequest_WhenCapacityChangeFails() {
        when(adminService.changeRoomCapacity(anyString(), any(RoomCapacityChangeRequest.class))).thenReturn(null);

        ResponseEntity<RoomResponse> response = adminController.changeRoomCapacity("room123", mockRoomCapacityChangeRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).changeRoomCapacity(eq("room123"), any(RoomCapacityChangeRequest.class));
    }
}