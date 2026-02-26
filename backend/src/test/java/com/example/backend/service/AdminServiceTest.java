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

    private UserResponse userResponse1;
    private UserResponse userResponse2;
    private UserRequest userRequest;
    private RoomResponse roomResponse1;
    private RoomResponse roomResponse2;
    private RoomRequest roomRequest;

    @BeforeEach
    void setUp() {
        userResponse1 = new UserResponse(1L, "john.doe@example.com", "John", "Doe", "ADMIN");
        userResponse2 = new UserResponse(2L, "jane.smith@example.com", "Jane", "Smith", "USER");
        userRequest = new UserRequest("updated@example.com", "Updated", "User", "USER");

        roomResponse1 = new RoomResponse("R101", "Conference Room 1", 10);
        roomResponse2 = new RoomResponse("R102", "Conference Room 2", 20);
        roomRequest = new RoomRequest("New Room", 15);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAllUsers()).thenReturn(Arrays.asList(userResponse1, userResponse2));

        List<UserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(userResponse1));

        UserResponse result = adminService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(1L);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.getUserById(99L));
        verify(userRepository, times(1)).findUserById(99L);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(userResponse1));
        when(userRepository.updateUser(anyLong(), any(UserRequest.class))).thenReturn(userResponse2);

        UserResponse result = adminService.updateUser(1L, userRequest);

        assertNotNull(result);
        assertEquals("jane.smith@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(1L);
        verify(userRepository, times(1)).updateUser(eq(1L), any(UserRequest.class));
    }

    @Test
    void updateUser_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.updateUser(99L, userRequest));
        verify(userRepository, times(1)).findUserById(99L);
        verify(userRepository, never()).updateUser(anyLong(), any(UserRequest.class));
    }

    @Test
    void deleteUser_shouldDeleteUserWhenExists() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> adminService.deleteUser(1L));
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteUser(99L));
        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void changeUserEmail_shouldReturnUserWithUpdatedEmail() {
        UserResponse updatedUser = new UserResponse(1L, "new.email@example.com", "John", "Doe", "ADMIN");
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(userResponse1));
        when(userRepository.changeUserEmail(anyLong(), anyString())).thenReturn(updatedUser);

        UserResponse result = adminService.changeUserEmail(1L, "new.email@example.com");

        assertNotNull(result);
        assertEquals("new.email@example.com", result.getEmail());
        verify(userRepository, times(1)).findUserById(1L);
        verify(userRepository, times(1)).changeUserEmail(eq(1L), eq("new.email@example.com"));
    }

    @Test
    void getAllRooms_shouldReturnListOfRooms() {
        when(roomRepository.findAllRooms()).thenReturn(Arrays.asList(roomResponse1, roomResponse2));

        List<RoomResponse> result = adminService.getAllRooms();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roomRepository, times(1)).findAllRooms();
    }

    @Test
    void createRoom_shouldReturnCreatedRoom() {
        when(roomRepository.createRoom(any(RoomRequest.class))).thenReturn(roomResponse1);

        RoomResponse result = adminService.createRoom(roomRequest);

        assertNotNull(result);
        assertEquals("R101", result.getRoomId());
        verify(roomRepository, times(1)).createRoom(roomRequest);
    }

    @Test
    void updateRoom_shouldReturnUpdatedRoom() {
        when(roomRepository.findRoomById(anyString())).thenReturn(Optional.of(roomResponse1));
        when(roomRepository.updateRoom(anyString(), any(RoomRequest.class))).thenReturn(roomResponse2);

        RoomResponse result = adminService.updateRoom("R101", roomRequest);

        assertNotNull(result);
        assertEquals("Conference Room 2", result.getName());
        verify(roomRepository, times(1)).findRoomById("R101");
        verify(roomRepository, times(1)).updateRoom(eq("R101"), any(RoomRequest.class));
    }

    @Test
    void updateRoom_shouldThrowExceptionWhenRoomNotFound() {
        when(roomRepository.findRoomById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.updateRoom("R999", roomRequest));
        verify(roomRepository, times(1)).findRoomById("R999");
        verify(roomRepository, never()).updateRoom(anyString(), any(RoomRequest.class));
    }

    @Test
    void deleteRoom_shouldDeleteRoomWhenExists() {
        when(roomRepository.existsById(anyString())).thenReturn(true);

        assertDoesNotThrow(() -> adminService.deleteRoom("R101"));
        verify(roomRepository, times(1)).existsById("R101");
        verify(roomRepository, times(1)).deleteById("R101");
    }

    @Test
    void deleteRoom_shouldThrowExceptionWhenRoomNotFound() {
        when(roomRepository.existsById(anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteRoom("R999"));
        verify(roomRepository, times(1)).existsById("R999");
        verify(roomRepository, never()).deleteById(anyString());
    }

    @Test
    void changeRoomName_shouldReturnRoomWithUpdatedName() {
        RoomResponse updatedRoom = new RoomResponse("R101", "New Room Name", 10);
        when(roomRepository.findRoomById(anyString())).thenReturn(Optional.of(roomResponse1));
        when(roomRepository.changeRoomName(anyString(), anyString())).thenReturn(updatedRoom);

        RoomResponse result = adminService.changeRoomName("R101", "New Room Name");

        assertNotNull(result);
        assertEquals("New Room Name", result.getName());
        verify(roomRepository, times(1)).findRoomById("R101");
        verify(roomRepository, times(1)).changeRoomName(eq("R101"), eq("New Room Name"));
    }

    @Test
    void changeRoomCapacity_shouldReturnRoomWithUpdatedCapacity() {
        RoomResponse updatedRoom = new RoomResponse("R101", "Conference Room 1", 25);
        when(roomRepository.findRoomById(anyString())).thenReturn(Optional.of(roomResponse1));
        when(roomRepository.changeRoomCapacity(anyString(), anyInt())).thenReturn(updatedRoom);

        RoomResponse result = adminService.changeRoomCapacity("R101", 25);

        assertNotNull(result);
        assertEquals(25, result.getCapacity());
        verify(roomRepository, times(1)).findRoomById("R101");
        verify(roomRepository, times(1)).changeRoomCapacity(eq("R101"), eq(25));
    }
}