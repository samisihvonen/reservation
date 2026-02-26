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
        userResponse1 = new UserResponse(1L, "John Doe", "john@example.com", "ADMIN");
        userResponse2 = new UserResponse(2L, "Jane Smith", "jane@example.com", "USER");
        userRequest = new UserRequest("Updated Name", "updated@example.com", "USER");

        roomResponse1 = new RoomResponse("A101", "Conference Room 1", 10);
        roomResponse2 = new RoomResponse("B202", "Meeting Room 2", 20);
        roomRequest = new RoomRequest("Updated Room", 15);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                new com.example.backend.entity.User(1L, "John Doe", "john@example.com", "ADMIN"),
                new com.example.backend.entity.User(2L, "Jane Smith", "jane@example.com", "USER")
        ));

        List<UserResponse> users = adminService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(
                new com.example.backend.entity.User(1L, "John Doe", "john@example.com", "ADMIN")
        ));

        UserResponse user = adminService.getUserById(1L);

        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.getUserById(99L));
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        com.example.backend.entity.User existingUser = new com.example.backend.entity.User(1L, "Old Name", "old@example.com", "ADMIN");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(com.example.backend.entity.User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse updatedUser = adminService.updateUser(1L, userRequest);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> adminService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenNotExists() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteUser(99L));
    }

    @Test
    void changeUserEmail_shouldUpdateEmail() {
        com.example.backend.entity.User existingUser = new com.example.backend.entity.User(1L, "John Doe", "old@example.com", "ADMIN");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(com.example.backend.entity.User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse updatedUser = adminService.changeUserEmail(1L, "new@example.com");

        assertNotNull(updatedUser);
        assertEquals("new@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void getAllRooms_shouldReturnListOfRooms() {
        when(roomRepository.findAll()).thenReturn(Arrays.asList(
                new com.example.backend.entity.Room("A101", "Conference Room 1", 10),
                new com.example.backend.entity.Room("B202", "Meeting Room 2", 20)
        ));

        List<RoomResponse> rooms = adminService.getAllRooms();

        assertEquals(2, rooms.size());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void createRoom_shouldCreateNewRoom() {
        when(roomRepository.existsById("A101")).thenReturn(false);
        when(roomRepository.save(any(com.example.backend.entity.Room.class))).thenAnswer(i -> i.getArgument(0));

        RoomResponse createdRoom = adminService.createRoom(new RoomRequest("Conference Room 1", 10));

        assertNotNull(createdRoom);
        assertEquals("Conference Room 1", createdRoom.getName());
        verify(roomRepository, times(1)).save(any(com.example.backend.entity.Room.class));
    }

    @Test
    void createRoom_shouldThrowExceptionWhenRoomExists() {
        when(roomRepository.existsById(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> adminService.createRoom(roomRequest));
    }

    @Test
    void updateRoom_shouldUpdateExistingRoom() {
        com.example.backend.entity.Room existingRoom = new com.example.backend.entity.Room("A101", "Old Name", 5);
        when(roomRepository.findById("A101")).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(com.example.backend.entity.Room.class))).thenAnswer(i -> i.getArgument(0));

        RoomResponse updatedRoom = adminService.updateRoom("A101", roomRequest);

        assertNotNull(updatedRoom);
        assertEquals("Updated Room", updatedRoom.getName());
        assertEquals(15, updatedRoom.getCapacity());
        verify(roomRepository, times(1)).save(existingRoom);
    }

    @Test
    void deleteRoom_shouldDeleteExistingRoom() {
        when(roomRepository.existsById("A101")).thenReturn(true);

        assertDoesNotThrow(() -> adminService.deleteRoom("A101"));
        verify(roomRepository, times(1)).deleteById("A101");
    }

    @Test
    void deleteRoom_shouldThrowExceptionWhenNotExists() {
        when(roomRepository.existsById(anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteRoom("XXX"));
    }

    @Test
    void changeRoomName_shouldUpdateName() {
        com.example.backend.entity.Room existingRoom = new com.example.backend.entity.Room("A101", "Old Name", 10);
        when(roomRepository.findById("A101")).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(com.example.backend.entity.Room.class))).thenAnswer(i -> i.getArgument(0));

        RoomResponse updatedRoom = adminService.changeRoomName("A101", "New Name");

        assertNotNull(updatedRoom);
        assertEquals("New Name", updatedRoom.getName());
        verify(roomRepository, times(1)).save(existingRoom);
    }

    @Test
    void changeRoomCapacity_shouldUpdateCapacity() {
        com.example.backend.entity.Room existingRoom = new com.example.backend.entity.Room("A101", "Conference Room", 5);
        when(roomRepository.findById("A101")).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(com.example.backend.entity.Room.class))).thenAnswer(i -> i.getArgument(0));

        RoomResponse updatedRoom = adminService.changeRoomCapacity("A101", 20);

        assertNotNull(updatedRoom);
        assertEquals(20, updatedRoom.getCapacity());
        verify(roomRepository, times(1)).save(existingRoom);
    }
}