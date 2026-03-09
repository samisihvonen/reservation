package com.example.backend.service;

import com.example.backend.dto.request.RoomRequest;
import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.RoomResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Room;
import com.example.backend.model.User;
import com.example.backend.repository.RoomRepository;
import com.example.backend.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdminService adminService;

    private User user1;
    private User user2;
    private UserResponse userResponse1;
    private UserResponse userResponse2;
    private UserRequest userRequest;
    private Room room1;
    private Room room2;
    private RoomResponse roomResponse1;
    private RoomResponse roomResponse2;
    private RoomRequest roomRequest;

    @BeforeEach
    public void setup() {
        user1 = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        userResponse1 = new UserResponse(1L, "John", "Doe", "john.doe@example.com");
        userResponse2 = new UserResponse(2L, "Jane", "Smith", "jane.smith@example.com");

        userRequest = new UserRequest("John", "Doe", "john.doe.updated@example.com");

        room1 = Room.builder()
                .roomId("R101")
                .name("Conference Room 1")
                .capacity(10)
                .build();

        room2 = Room.builder()
                .roomId("R102")
                .name("Conference Room 2")
                .capacity(20)
                .build();

        roomResponse1 = new RoomResponse("R101", "Conference Room 1", 10);
        roomResponse2 = new RoomResponse("R102", "Conference Room 2", 20);

        roomRequest = new RoomRequest("Updated Conference Room", 15);
    }

    @Test
    public void getAllUsers_shouldReturnListOfUserResponse() {
        // Given
        List<User> users = Arrays.asList(user1, user2);
        given(userRepository.findAll()).willReturn(users);
        given(modelMapper.map(user1, UserResponse.class)).willReturn(userResponse1);
        given(modelMapper.map(user2, UserResponse.class)).willReturn(userResponse2);

        // When
        List<UserResponse> result = adminService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getUserById_shouldReturnUserResponse_whenUserExists() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(modelMapper.map(user1, UserResponse.class)).willReturn(userResponse1);

        // When
        UserResponse result = adminService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(userResponse1, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void getUserById_shouldThrowException_whenUserDoesNotExist() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> adminService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void updateUser_shouldReturnUpdatedUserResponse_whenUserExists() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(userRepository.save(any(User.class))).willReturn(user1);
        given(modelMapper.map(userRequest, User.class)).willReturn(user1);
        given(modelMapper.map(user1, UserResponse.class)).willReturn(userResponse1);

        // When
        UserResponse result = adminService.updateUser(1L, userRequest);

        // Then
        assertNotNull(result);
        assertEquals(userResponse1, result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_shouldThrowException_whenUserDoesNotExist() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> adminService.updateUser(1L, userRequest));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void deleteUser_shouldDeleteUser_whenUserExists() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        willDoNothing().given(userRepository).delete(any(User.class));

        // When & Then
        assertDoesNotThrow(() -> adminService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    public void deleteUser_shouldThrowException_whenUserDoesNotExist() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void changeUserEmail_shouldReturnUpdatedUserResponse_whenUserExists() {
        // Given
        String newEmail = "new.email@example.com";
        user1.setEmail(newEmail);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));
        given(userRepository.save(any(User.class))).willReturn(user1);
        given(modelMapper.map(user1, UserResponse.class)).willReturn(userResponse1);

        // When
        UserResponse result = adminService.changeUserEmail(1L, newEmail);

        // Then
        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void changeUserEmail_shouldThrowException_whenUserDoesNotExist() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> adminService.changeUserEmail(1L, "new.email@example.com"));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void getAllRooms_shouldReturnListOfRoomResponse() {
        // Given
        List<Room> rooms = Arrays.asList(room1, room2);
        given(roomRepository.findAll()).willReturn(rooms);
        given(modelMapper.map(room1, RoomResponse.class)).willReturn(roomResponse1);
        given(modelMapper.map(room2, RoomResponse.class)).willReturn(roomResponse2);

        // When
        List<RoomResponse> result = adminService.getAllRooms();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    public void createRoom_shouldReturnRoomResponse() {
        // Given
        given(roomRepository.save(any(Room.class))).willReturn(room1);
        given(modelMapper.map(roomRequest, Room.class)).willReturn(room1);
        given(modelMapper.map(room1, RoomResponse.class)).willReturn(roomResponse1);

        // When
        RoomResponse result = adminService.createRoom(roomRequest);

        // Then
        assertNotNull(result);
        assertEquals(roomResponse1, result);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    public void updateRoom_shouldReturnUpdatedRoomResponse_whenRoomExists() {
        // Given
        given(roomRepository.findById(anyString())).willReturn(Optional.of(room1));
        given(roomRepository.save(any(Room.class))).willReturn(room1);
        given(modelMapper.map(roomRequest, Room.class)).willReturn(room1);
        given(modelMapper.map(room1, RoomResponse.class)).willReturn(roomResponse1);

        // When
        RoomResponse result = adminService.updateRoom("R101", roomRequest);

        // Then
        assertNotNull(result);
        assertEquals(roomResponse1, result);
        verify(roomRepository, times(1)).findById("R101");
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    public void updateRoom_shouldThrowException_whenRoomDoesNotExist() {
        // Given
        given(roomRepository.findById(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> adminService.updateRoom("R101", roomRequest));
        verify(roomRepository, times(1)).findById("R101");
    }

    @Test
    public void deleteRoom_shouldDeleteRoom_whenRoomExists() {
        // Given
        given(roomRepository.findById(anyString())).willReturn(Optional.of(room1));
        willDoNothing().given(roomRepository).delete(any(Room.class));

        // When & Then
        assertDoesNotThrow(() -> adminService.deleteRoom("R101"));
        verify(roomRepository, times(1)).findById("R101");
        verify(roomRepository, times(1)).delete(any(Room.class));
    }

    @Test
    public void deleteRoom_shouldThrowException_whenRoomDoesNotExist() {
        // Given
        given(roomRepository.findById(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteRoom("R101"));
        verify(roomRepository, times(1)).findById("R101");
    }

    @Test
    public void changeRoomName_shouldReturnUpdatedRoomResponse_whenRoomExists() {
        // Given
        String newName = "Updated Room Name";
        room1.setName(newName);
        given(roomRepository.findById(anyString())).willReturn(Optional.of(room1));
        given(roomRepository.save(any(Room.class))).willReturn(room1);
        given(modelMapper.map(room1, RoomResponse.class)).willReturn(roomResponse1);

        // When
        RoomResponse result = adminService.changeRoomName("R101", newName);

        // Then
        assertNotNull(result);
        assertEquals(newName, result.getName());
        verify(roomRepository, times(1)).findById("R101");
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    public void changeRoomName_shouldThrowException_whenRoomDoesNotExist() {
        // Given
        given(roomRepository.findById(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> adminService.changeRoomName("R101", "New Name"));
        verify(roomRepository, times(1)).findById("R101");
    }

    @Test
    public void changeRoomCapacity_shouldReturnUpdatedRoomResponse_whenRoomExists() {
        // Given
        int newCapacity = 25;
        room1.setCapacity(newCapacity);
        given(roomRepository.findById(anyString())).willReturn(Optional.of(room1));
        given(roomRepository.save(any(Room.class))).willReturn(room1);
        given(modelMapper.map(room1, RoomResponse.class)).willReturn(roomResponse1);

        // When
        RoomResponse result = adminService.changeRoomCapacity("R101", newCapacity);

        // Then
        assertNotNull(result);
        assertEquals(newCapacity, result.getCapacity());
        verify(roomRepository, times(1)).findById("R101");
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    public void changeRoomCapacity_shouldThrowException_whenRoomDoesNotExist() {
        // Given
        given(roomRepository.findById(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> adminService.changeRoomCapacity("R101", 25));
        verify(roomRepository, times(1)).findById("R101");
    }
}