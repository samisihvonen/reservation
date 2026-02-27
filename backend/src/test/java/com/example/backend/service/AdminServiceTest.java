package com.example.backend.service;

import com.example.backend.model.request.RoomRequest;
import com.example.backend.model.request.UserRequest;
import com.example.backend.model.response.RoomResponse;
import com.example.backend.model.response.UserResponse;
import com.example.backend.repository.RoomRepository;
import com.example.backend.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminService adminService;

    private UserResponse userResponse1;
    private UserResponse userResponse2;
    private UserRequest userRequest;
    private RoomResponse roomResponse1;
    private RoomResponse roomResponse2;
    private RoomRequest roomRequest;

    @BeforeEach
    void setUp() {
        userResponse1 = new UserResponse(1L, "user1@example.com", "User", "One");
        userResponse2 = new UserResponse(2L, "user2@example.com", "User", "Two");
        userRequest = new UserRequest("newuser@example.com", "New", "User");

        roomResponse1 = new RoomResponse("ROOM001", "Conference Room 1", 10);
        roomResponse2 = new RoomResponse("ROOM002", "Conference Room 2", 20);
        roomRequest = new RoomRequest("New Room", 15);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAllUsers()).thenReturn(Arrays.asList(userResponse1, userResponse2));

        List<UserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(userResponse1));

        UserResponse result = adminService.getUserById(1L);

        assertNotNull(result);
        assertEquals("user1@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.getUserById(99L));
        verify(userRepository, times(1)).findUserById(99L);
    }

    @Test
    void updateUser_WithValidIdAndRequest_ShouldReturnUpdatedUser() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(userResponse1));
        when(userRepository.save(any())).thenReturn(userResponse1);

        UserResponse result = adminService.updateUser(1L, userRequest);

        assertNotNull(result);
        assertEquals("user1@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(1L);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.updateUser(99L, userRequest));
        verify(userRepository, times(1)).findUserById(99L);
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> adminService.deleteUser(1L));
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteUser(99L));
        verify(userRepository, times(1)).existsById(99L);
    }

    @Test
    void changeUserEmail_WithValidIdAndEmail_ShouldReturnUpdatedUser() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(userResponse1));
        when(userRepository.save(any())).thenReturn(
            new UserResponse(1L, "newemail@example.com", "User", "One")
        );

        UserResponse result = adminService.changeUserEmail(1L, "newemail@example.com");

        assertNotNull(result);
        assertEquals("newemail@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(1L);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getAllRooms_ShouldReturnListOfRooms() {
        when(roomRepository.findAllRooms()).thenReturn(Arrays.asList(roomResponse1, roomResponse2));

        List<RoomResponse> result = adminService.getAllRooms();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roomRepository, times(1)).findAllRooms();
    }

    @Test
    void createRoom_WithValidRequest_ShouldReturnCreatedRoom() {
        when(roomRepository.save(any())).thenReturn(roomResponse1);

        RoomResponse result = adminService.createRoom(roomRequest);

        assertNotNull(result);
        assertEquals("ROOM001", result.getRoomId());
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void updateRoom_WithValidIdAndRequest_ShouldReturnUpdatedRoom() {
        when(roomRepository.findRoomById(anyString())).thenReturn(Optional.of(roomResponse1));
        when(roomRepository.save(any())).thenReturn(roomResponse1);

        RoomResponse result = adminService.updateRoom("ROOM001", roomRequest);

        assertNotNull(result);
        assertEquals("ROOM001", result.getRoomId());
        verify(roomRepository, times(1)).findRoomById("ROOM001");
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void updateRoom_WithInvalidId_ShouldThrowException() {
        when(roomRepository.findRoomById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.updateRoom("INVALID", roomRequest));
        verify(roomRepository, times(1)).findRoomById("INVALID");
    }

    @Test
    void deleteRoom_WithValidId_ShouldDeleteRoom() {
        when(roomRepository.existsById(anyString())).thenReturn(true);

        assertDoesNotThrow(() -> adminService.deleteRoom("ROOM001"));
        verify(roomRepository, times(1)).existsById("ROOM001");
        verify(roomRepository, times(1)).deleteById("ROOM001");
    }

    @Test
    void deleteRoom_WithInvalidId_ShouldThrowException() {
        when(roomRepository.existsById(anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteRoom("INVALID"));
        verify(roomRepository, times(1)).existsById("INVALID");
    }

    @Test
    void changeRoomName_WithValidIdAndName_ShouldReturnUpdatedRoom() {
        when(roomRepository.findRoomById(anyString())).thenReturn(Optional.of(roomResponse1));
        when(roomRepository.save(any())).thenReturn(
            new RoomResponse("ROOM001", "New Room Name", 10)
        );

        RoomResponse result = adminService.changeRoomName("ROOM001", "New Room Name");

        assertNotNull(result);
        assertEquals("New Room Name", result.getName());
        verify(roomRepository, times(1)).findRoomById("ROOM001");
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void changeRoomCapacity_WithValidIdAndCapacity_ShouldReturnUpdatedRoom() {
        when(roomRepository.findRoomById(anyString())).thenReturn(Optional.of(roomResponse1));
        when(roomRepository.save(any())).thenReturn(
            new RoomResponse("ROOM001", "Conference Room 1", 25)
        );

        RoomResponse result = adminService.changeRoomCapacity("ROOM001", 25);

        assertNotNull(result);
        assertEquals(25, result.getCapacity());
        verify(roomRepository, times(1)).findRoomById("ROOM001");
        verify(roomRepository, times(1)).save(any());
    }
}