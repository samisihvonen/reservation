package com.example.backend.service;

import com.example.backend.dto.request.RoomCapacityChangeRequest;
import com.example.backend.dto.request.RoomRequest;
import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.RoomResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.exception.ReservationException;
import com.example.backend.model.Room;
import com.example.backend.model.User;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminService adminService;

    private User user1;
    private User user2;
    private UserRequest userRequest;
    private Room room1;
    private RoomRequest roomRequest;

    @BeforeEach
    public void setup() {
        // User Setup - Using standard constructors as per User.java
        user1 = new User("john.doe@example.com", "password", "John Doe");
        user1.setId(1L);
        
        user2 = new User("jane.smith@example.com", "password", "Jane Smith");
        user2.setId(2L);

        // UserRequest Setup - Aligned with UserRequest.java
        userRequest = new UserRequest("john.updated@example.com", "John Updated");

        // Room Setup - Aligned with Room.java
        room1 = new Room("R101", "Conference Room 1", 10);

        // RoomRequest Setup - Aligned with RoomRequest.java
        roomRequest = new RoomRequest();
        roomRequest.setName("Updated Room");
        roomRequest.setCapacity(15);
    }

    @Test
    public void getAllUsers_shouldReturnListOfUserResponse() {
        // Given
        List<User> users = Arrays.asList(user1, user2);
        given(userRepository.findAll()).willReturn(users);

        // When
        List<UserResponse> result = adminService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getDisplayName()); // Verified against UserResponse
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getUserById_shouldReturnUserResponse_whenUserExists() {
        // Given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user1));

        // When
        UserResponse result = adminService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getDisplayName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void getUserById_shouldThrowException_whenUserDoesNotExist() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ReservationException.class, () -> adminService.getUserById(1L));
    }

    @Test
    public void updateUser_shouldReturnUpdatedUserResponse_whenUserExists() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.of(user1));
        given(userRepository.save(any(User.class))).willReturn(user1);

        // When
        UserResponse result = adminService.updateUser(1L, userRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void createRoom_shouldReturnRoomResponse() {
        // Given
        given(roomRepository.save(any(Room.class))).willReturn(room1);

        // When
        RoomResponse result = adminService.createRoom(roomRequest);

        // Then
        assertNotNull(result);
        assertEquals("R101", result.getId()); // Verified against RoomResponse
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    public void changeRoomCapacity_shouldReturnUpdatedRoomResponse_whenRoomExists() {
        // Given
        RoomCapacityChangeRequest capacityRequest = new RoomCapacityChangeRequest();
        capacityRequest.setNewCapacity(25); // Aligned with RoomCapacityChangeRequest

        given(roomRepository.findById("R101")).willReturn(Optional.of(room1));
        given(roomRepository.save(any(Room.class))).willReturn(room1);

        // When
        RoomResponse result = adminService.changeRoomCapacity("R101", capacityRequest);

        // Then
        assertNotNull(result);
        verify(roomRepository).save(any(Room.class));
    }
}