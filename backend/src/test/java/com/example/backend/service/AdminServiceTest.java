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

    private UserRequest userRequest;
    private UserResponse userResponse;
    private RoomRequest roomRequest;
    private RoomResponse roomResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("testUser", "test@example.com", "password");
        userResponse = new UserResponse(1L, "testUser", "test@example.com");
        roomRequest = new RoomRequest("Test Room", 10);
        roomResponse = new RoomResponse("ROOM1", "Test Room", 10);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAllProjectedBy()).thenReturn(Arrays.asList(userResponse));

        List<UserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAllProjectedBy();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        when(userRepository.findProjectedById(anyLong())).thenReturn(Optional.of(userResponse));

        UserResponse result = adminService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userRepository, times(1)).findProjectedById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        when(userRepository.findProjectedById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.getUserById(999L));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new com.example.backend.entity.User()));
        when(userRepository.save(any())).thenReturn(new com.example.backend.entity.User(1L, "updatedUser", "updated@example.com", "password"));

        UserRequest updateRequest = new UserRequest("updatedUser", "updated@example.com", "newPassword");
        UserResponse result = adminService.updateUser(1L, updateRequest);

        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> adminService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteUser(999L));
    }

    @Test
    void changeUserEmail_WithValidData_ShouldReturnUpdatedUser() {
        com.example.backend.entity.User user = new com.example.backend.entity.User(1L, "testUser", "old@example.com", "password");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(new com.example.backend.entity.User(1L, "testUser", "new@example.com", "password"));

        UserResponse result = adminService.changeUserEmail(1L, "new@example.com");

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getAllRooms_ShouldReturnListOfRooms() {
        when(roomRepository.findAllProjectedBy()).thenReturn(Arrays.asList(roomResponse));

        List<RoomResponse> result = adminService.getAllRooms();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomRepository, times(1)).findAllProjectedBy();
    }

    @Test
    void createRoom_WithValidData_ShouldReturnCreatedRoom() {
        when(roomRepository.save(any())).thenReturn(new com.example.backend.entity.Room("ROOM1", "Test Room", 10));

        RoomResponse result = adminService.createRoom(roomRequest);

        assertNotNull(result);
        assertEquals("Test Room", result.getName());
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void updateRoom_WithValidData_ShouldReturnUpdatedRoom() {
        com.example.backend.entity.Room room = new com.example.backend.entity.Room("ROOM1", "Old Name", 5);
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(new com.example.backend.entity.Room("ROOM1", "New Name", 15));

        RoomRequest updateRequest = new RoomRequest("New Name", 15);
        RoomResponse result = adminService.updateRoom("ROOM1", updateRequest);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals(15, result.getCapacity());
        verify(roomRepository, times(1)).findById("ROOM1");
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void deleteRoom_WithValidId_ShouldDeleteRoom() {
        when(roomRepository.existsById(anyString())).thenReturn(true);

        assertDoesNotThrow(() -> adminService.deleteRoom("ROOM1"));
        verify(roomRepository, times(1)).deleteById("ROOM1");
    }

    @Test
    void changeRoomName_WithValidData_ShouldReturnUpdatedRoom() {
        com.example.backend.entity.Room room = new com.example.backend.entity.Room("ROOM1", "Old Name", 10);
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(new com.example.backend.entity.Room("ROOM1", "New Name", 10));

        RoomResponse result = adminService.changeRoomName("ROOM1", "New Name");

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(roomRepository, times(1)).findById("ROOM1");
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void changeRoomCapacity_WithValidData_ShouldReturnUpdatedRoom() {
        com.example.backend.entity.Room room = new com.example.backend.entity.Room("ROOM1", "Test Room", 5);
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(new com.example.backend.entity.Room("ROOM1", "Test Room", 20));

        RoomResponse result = adminService.changeRoomCapacity("ROOM1", 20);

        assertNotNull(result);
        assertEquals(20, result.getCapacity());
        verify(roomRepository, times(1)).findById("ROOM1");
        verify(roomRepository, times(1)).save(any());
    }
}