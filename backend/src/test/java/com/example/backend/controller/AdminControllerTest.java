package com.example.backend.controller;

import com.example.backend.dto.request.EmailChangeRequest;
import com.example.backend.dto.request.RoomCapacityChangeRequest;
import com.example.backend.dto.request.RoomRequest;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private UserResponse userResponse;
    private RoomResponse roomResponse;
    private RoomRequest roomRequest;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(1L, "test@example.com", "John Doe", LocalDateTime.now());
        roomResponse = new RoomResponse(
                "ROOM1",
                "Conference Room 1",
                10,
                "Main room",
                "Floor 1",
                true,
                LocalDateTime.now(),
                LocalDateTime.now());

        roomRequest = new RoomRequest();
        roomRequest.setName("Conference Room 1");
        roomRequest.setCapacity(10);
        roomRequest.setDescription("Main room");
        roomRequest.setLocation("Floor 1");
    }

    @Test
    void getUserById_ShouldReturnUserResponse() {
        when(adminService.getUserById(anyLong())).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = adminController.getUserById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    void createRoom_ShouldReturnCreatedRoomResponse() {
        when(adminService.createRoom(any(RoomRequest.class))).thenReturn(roomResponse);

        ResponseEntity<RoomResponse> response = adminController.createRoom(roomRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(roomResponse, response.getBody());
    }

    @Test
    void changeUserEmail_ShouldForwardNewEmailValue() {
        EmailChangeRequest request = new EmailChangeRequest();
        request.setNewEmail("new.email@example.com");
        when(adminService.changeUserEmail(anyLong(), anyString())).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = adminController.changeUserEmail(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(adminService).changeUserEmail(1L, "new.email@example.com");
    }

    @Test
    void changeRoomCapacity_ShouldForwardCapacityValue() {
        RoomCapacityChangeRequest request = new RoomCapacityChangeRequest();
        request.setNewCapacity(20);
        RoomResponse updatedRoom = new RoomResponse(
                "ROOM1",
                "Conference Room 1",
                20,
                "Main room",
                "Floor 1",
                true,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(adminService.changeRoomCapacity(anyString(), any())).thenReturn(updatedRoom);

        ResponseEntity<RoomResponse> response = adminController.changeRoomCapacity("ROOM1", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20, response.getBody().getCapacity());
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        ResponseEntity<Void> response = adminController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adminService).deleteUser(1L);
    }
}
