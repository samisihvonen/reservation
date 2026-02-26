package com.example.backend.service;

import com.example.backend.dto.RoomRequest;
import com.example.backend.dto.RoomResponse;
import com.example.backend.dto.UserRequest;
import com.example.backend.dto.UserResponse;
import com.example.backend.exception.ReservationException;
import com.example.backend.model.Room;
import com.example.backend.model.User;
import com.example.backend.repository.RoomRepository;
import com.example.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminService adminService;

    private User user;
    private UserResponse userResponse;
    private Room room;
    private RoomResponse roomResponse;
    private RoomRequest roomRequest;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");
        user.setCreatedAt(LocalDateTime.now());

        userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt()
        );

        room = new Room();
        room.setId("room-12345678");
        room.setName("Test Room");
        room.setCapacity(10);
        room.setDescription("Test Description");
        room.setLocation("Test Location");
        room.setIsActive(true);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());

        roomResponse = new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getDescription(),
                room.getLocation(),
                room.getIsActive(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );

        roomRequest = new RoomRequest();
        roomRequest.setName("New Room");
        roomRequest.setCapacity(15);
        roomRequest.setDescription("New Description");
        roomRequest.setLocation("New Location");

        userRequest = new UserRequest();
        userRequest.setEmail("new@example.com");
        userRequest.setDisplayName("New User");
    }

    @Test
    @DisplayName("getAllUsers should return list of all users")
    void getAllUsers_shouldReturnListOfAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = adminService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getUserById should return user when user exists")
    void getUserById_shouldReturnUserWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = adminService.getUserById(1L);

        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getUserById should throw exception when user does not exist")
    void getUserById_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getUserById(1L))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Käyttäjää ei löydy ID:llä: 1");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateUser should update user when user exists and email is not in use")
    void updateUser_shouldUpdateUserWhenUserExistsAndEmailIsNotInUse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = adminService.updateUser(1L, userRequest);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getDisplayName()).isEqualTo("New User");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("updateUser should throw exception when user does not exist")
    void updateUser_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateUser(1L, userRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Käyttäjää ei löydy ID:llä: 1");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateUser should throw exception when email is already in use")
    void updateUser_shouldThrowExceptionWhenEmailIsAlreadyInUse() {
        userRequest.setEmail("existing@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> adminService.updateUser(1L, userRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Sähköposti on jo käytössä");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateUser should update only display name when email is null")
    void updateUser_shouldUpdateOnlyDisplayNameWhenEmailIsNull() {
        userRequest.setEmail(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = adminService.updateUser(1L, userRequest);

        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getDisplayName()).isEqualTo("New User");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("deleteUser should delete user when user exists")
    void deleteUser_shouldDeleteUserWhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        adminService.deleteUser(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser should throw exception when user does not exist")
    void deleteUser_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> adminService.deleteUser(1L))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Käyttäjää ei löydy ID:llä: 1");
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("changeUserEmail should update email when user exists and email is not in use")
    void changeUserEmail_shouldUpdateEmailWhenUserExistsAndEmailIsNotInUse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = adminService.changeUserEmail(1L, "new@example.com");

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("changeUserEmail should throw exception when user does not exist")
    void changeUserEmail_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.changeUserEmail(1L, "new@example.com"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Käyttäjää ei löydy ID:llä: 1");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changeUserEmail should throw exception when email is already in use")
    void changeUserEmail_shouldThrowExceptionWhenEmailIsAlreadyInUse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> adminService.changeUserEmail(1L, "existing@example.com"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Sähköposti on jo käytössä");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getAllRooms should return list of all active rooms")
    void getAllRooms_shouldReturnListOfAllActiveRooms() {
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomResponse> result = adminService.getAllRooms();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(room.getName());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllRooms should not return inactive rooms")
    void getAllRooms_shouldNotReturnInactiveRooms() {
        Room inactiveRoom = new Room();
        inactiveRoom.setIsActive(false);
        when(roomRepository.findAll()).thenReturn(List.of(room, inactiveRoom));

        List<RoomResponse> result = adminService.getAllRooms();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createRoom should create new room with generated ID")
    void createRoom_shouldCreateNewRoomWithGeneratedID() {
        when(roomRepository.existsById(anyString())).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse result = adminService.createRoom(roomRequest);

        assertThat(result.getName()).isEqualTo(roomRequest.getName());
        assertThat(result.getCapacity()).isEqualTo(roomRequest.getCapacity());
        assertThat(result.getId()).startsWith("room-");
        verify(roomRepository, times(1)).existsById(anyString());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom should throw exception when room ID already exists")
    void createRoom_shouldThrowExceptionWhenRoomIDAlreadyExists() {
        when(roomRepository.existsById(anyString())).thenReturn(true);

        assertThatThrownBy(() -> adminService.createRoom(roomRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huone ID on jo olemassa");
        verify(roomRepository, times(1)).existsById(anyString());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("updateRoom should update room when room exists")
    void updateRoom_shouldUpdateRoomWhenRoomExists() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse result = adminService.updateRoom("room-12345678", roomRequest);

        assertThat(result.getName()).isEqualTo(roomRequest.getName());
        assertThat(result.getCapacity()).isEqualTo(roomRequest.getCapacity());
        assertThat(result.getDescription()).isEqualTo(roomRequest.getDescription());
        assertThat(result.getLocation()).isEqualTo(roomRequest.getLocation());
        verify(roomRepository, times(1)).findById("room-12345678");
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("updateRoom should throw exception when room does not exist")
    void updateRoom_shouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateRoom("room-12345678", roomRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huonetta ei löydy ID:llä: room-12345678");
        verify(roomRepository, times(1)).findById("room-12345678");
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("updateRoom should update only provided fields")
    void updateRoom_shouldUpdateOnlyProvidedFields() {
        RoomRequest partialRequest = new RoomRequest();
        partialRequest.setName("Partial Update");
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse result = adminService.updateRoom("room-12345678", partialRequest);

        assertThat(result.getName()).isEqualTo("Partial Update");
        assertThat(result.getCapacity()).isEqualTo(room.getCapacity());
        assertThat(result.getDescription()).isEqualTo(room.getDescription());
        verify(roomRepository, times(1)).findById("room-12345678");
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("deleteRoom should deactivate room when room exists")
    void deleteRoom_shouldDeactivateRoomWhenRoomExists() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        adminService.deleteRoom("room-12345678");

        assertThat(room.getIsActive()).isFalse();
        verify(roomRepository, times(1)).findById("room-12345678");
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("deleteRoom should throw exception when room does not exist")
    void deleteRoom_shouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.deleteRoom("room-12345678"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huonetta ei löydy ID:llä: room-12345678");
        verify(roomRepository, times(1)).findById("room-12345678");
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("changeRoomName should update room name when room exists")
    void changeRoomName_shouldUpdateRoomNameWhenRoomExists() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse result = adminService.changeRoomName("room-12345678", "New Room Name");

        assertThat(result.getName()).isEqualTo("New Room Name");
        verify(roomRepository, times(1)).findById("room-12345678");
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("changeRoomName should throw exception when room does not exist")
    void changeRoomName_shouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.changeRoomName("room-12345678", "New Room Name"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huonetta ei löydy ID:llä: room-12345678");
        verify(roomRepository, times(1)).findById("room-12345678");
        verify(room