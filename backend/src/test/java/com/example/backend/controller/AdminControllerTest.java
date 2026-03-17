package com.example.backend.controller;

import com.example.backend.dto.request.RoomCapacityChangeRequest;
import com.example.backend.dto.response.RoomResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.service.AdminService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AdminControllerTest {

    private static class StubAdminService extends AdminService {
        private UserResponse userResponse;
        private RoomResponse roomResponse;
        private Long deletedUserId;
        private String changedRoomId;
        private RoomCapacityChangeRequest changedCapacityRequest;

        StubAdminService() {
            super(null, null);
        }

        @Override
        public UserResponse getUserById(Long id) {
            return userResponse;
        }

        @Override
        public void deleteUser(Long id) {
            deletedUserId = id;
        }

        @Override
        public RoomResponse changeRoomCapacity(String roomId, RoomCapacityChangeRequest request) {
            changedRoomId = roomId;
            changedCapacityRequest = request;
            return roomResponse;
        }
    }

    private StubAdminService adminService;
    private AdminController adminController;
    private UserResponse userResponse;
    private RoomResponse roomResponse;
    private RoomCapacityChangeRequest roomCapacityChangeRequest;

    @BeforeEach
    void setUp() {
        adminService = new StubAdminService();
        adminController = new AdminController(adminService);

        LocalDateTime now = LocalDateTime.now();
        userResponse = new UserResponse(1L, "test@example.com", "John Doe", now);
        roomResponse = new RoomResponse("ROOM1", "Conference Room 1", 20, "Desc", "Loc", true, now, now);
        roomCapacityChangeRequest = new RoomCapacityChangeRequest();
        roomCapacityChangeRequest.setNewCapacity(20);

        adminService.userResponse = userResponse;
        adminService.roomResponse = roomResponse;
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenUserExists() {
        ResponseEntity<UserResponse> response = adminController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    void deleteUser_shouldReturnNoContent_whenDeletionIsSuccessful() {
        ResponseEntity<Void> response = adminController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        assertEquals(1L, adminService.deletedUserId);
    }

    @Test
    void changeRoomCapacity_shouldReturnUpdatedRoomResponse_whenCapacityChangeIsSuccessful() {
        ResponseEntity<RoomResponse> response = adminController.changeRoomCapacity("ROOM1", roomCapacityChangeRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roomResponse, response.getBody());
        assertEquals("ROOM1", adminService.changedRoomId);
        assertEquals(roomCapacityChangeRequest, adminService.changedCapacityRequest);
    }
}