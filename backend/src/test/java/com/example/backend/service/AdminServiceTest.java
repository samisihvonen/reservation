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
import org.modelmapper.ModelMapper;
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

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdminService adminService;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private RoomRequest roomRequest;
    private RoomResponse roomResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("John Doe", "john.doe@example.com", "password123");
        userResponse = new UserResponse(1L, "John Doe", "john.doe@example.com");
        roomRequest = new RoomRequest("Meeting Room 1", 10);
        roomResponse = new RoomResponse("MR001", "Meeting Room 1", 10);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserResponse() {
        List<UserResponse> expectedUsers = Arrays.asList(userResponse);
        when(userRepository.findAll()).thenReturn(Arrays.asList(new com.example.backend.entity.User()));
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(userResponse);

        List<UserResponse> actualUsers = adminService.getAllUsers();

        assertNotNull(actualUsers);
        assertEquals(1, actualUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUserResponse() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new com.example.backend.entity.User()));
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(userResponse);

        UserResponse actualUser = adminService.getUserById(1L);

        assertNotNull(actualUser);
        assertEquals(userResponse.getName(), actualUser.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.getUserById(999L));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUserResponse() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new com.example.backend.entity.User()));
        when(userRepository.save(any())).thenReturn(new com.example.backend.entity.User());
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(userResponse);

        UserResponse updatedUser = adminService.updateUser(1L, userRequest);

        assertNotNull(updatedUser);
        assertEquals(userResponse.getName(), updatedUser.getName());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> adminService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteUser(999L));
    }

    @Test
    void changeUserEmail_WithValidData_ShouldReturnUpdatedUserResponse() {
        com.example.backend.entity.User user = new com.example.backend.entity.User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(userResponse);

        UserResponse updatedUser = adminService.changeUserEmail(1L, "new.email@example.com");

        assertNotNull(updatedUser);
        assertEquals(userResponse.getName(), updatedUser.getName());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getAllRooms_ShouldReturnListOfRoomResponse() {
        List<RoomResponse> expectedRooms = Arrays.asList(roomResponse);
        when(roomRepository.findAll()).thenReturn(Arrays.asList(new com.example.backend.entity.Room()));
        when(modelMapper.map(any(), eq(RoomResponse.class))).thenReturn(roomResponse);

        List<RoomResponse> actualRooms = adminService.getAllRooms();

        assertNotNull(actualRooms);
        assertEquals(1, actualRooms.size());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void createRoom_WithValidData_ShouldReturnRoomResponse() {
        when(roomRepository.save(any())).thenReturn(new com.example.backend.entity.Room());
        when(modelMapper.map(any(), eq(RoomResponse.class))).thenReturn(roomResponse);

        RoomResponse createdRoom = adminService.createRoom(roomRequest);

        assertNotNull(createdRoom);
        assertEquals(roomResponse.getName(), createdRoom.getName());
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void updateRoom_WithValidData_ShouldReturnUpdatedRoomResponse() {
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(new com.example.backend.entity.Room()));
        when(roomRepository.save(any())).thenReturn(new com.example.backend.entity.Room());
        when(modelMapper.map(any(), eq(RoomResponse.class))).thenReturn(roomResponse);

        RoomResponse updatedRoom = adminService.updateRoom("MR001", roomRequest);

        assertNotNull(updatedRoom);
        assertEquals(roomResponse.getName(), updatedRoom.getName());
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void deleteRoom_WithValidId_ShouldDeleteRoom() {
        when(roomRepository.existsById(anyString())).thenReturn(true);
        doNothing().when(roomRepository).deleteById(anyString());

        assertDoesNotThrow(() -> adminService.deleteRoom("MR001"));
        verify(roomRepository, times(1)).deleteById("MR001");
    }

    @Test
    void deleteRoom_WithInvalidId_ShouldThrowException() {
        when(roomRepository.existsById(anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteRoom("INVALID"));
    }

    @Test
    void changeRoomName_WithValidData_ShouldReturnUpdatedRoomResponse() {
        com.example.backend.entity.Room room = new com.example.backend.entity.Room();
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(room);
        when(modelMapper.map(any(), eq(RoomResponse.class))).thenReturn(roomResponse);

        RoomResponse updatedRoom = adminService.changeRoomName("MR001", "New Room Name");

        assertNotNull(updatedRoom);
        assertEquals(roomResponse.getName(), updatedRoom.getName());
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void changeRoomCapacity_WithValidData_ShouldReturnUpdatedRoomResponse() {
        com.example.backend.entity.Room room = new com.example.backend.entity.Room();
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(room);
        when(modelMapper.map(any(), eq(RoomResponse.class))).thenReturn(roomResponse);

        RoomResponse updatedRoom = adminService.changeRoomCapacity("MR001", 20);

        assertNotNull(updatedRoom);
        assertEquals(roomResponse.getCapacity(), updatedRoom.getCapacity());
        verify(roomRepository, times(1)).save(any());
    }
}