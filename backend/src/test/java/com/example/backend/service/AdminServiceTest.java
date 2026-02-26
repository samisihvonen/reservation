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
    private UserRequest userRequest;
    private RoomRequest roomRequest;

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

        userRequest = new UserRequest();
        userRequest.setEmail("new@example.com");
        userRequest.setDisplayName("New User");

        roomRequest = new RoomRequest();
        roomRequest.setName("New Room");
        roomRequest.setCapacity(20);
        roomRequest.setDescription("New Description");
        roomRequest.setLocation("New Location");
    }

    @Test
    @DisplayName("getAllUsers should return list of all users")
    void getAllUsers_shouldReturnListOfAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = adminService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo(user.getEmail());
        assertThat(result.get(0).getDisplayName()).isEqualTo(user.getDisplayName());
    }

    @Test
    @DisplayName("getAllUsers should return empty list when no users exist")
    void getAllUsers_shouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = adminService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getUserById should return user when user exists")
    void getUserById_shouldReturnUserWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = adminService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getDisplayName()).isEqualTo(user.getDisplayName());
    }

    @Test
    @DisplayName("getUserById should throw exception when user does not exist")
    void getUserById_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getUserById(1L))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Käyttäjää ei löydy ID:llä: 1");
    }

    @Test
    @DisplayName("updateUser should update user when user exists and email is not taken")
    void updateUser_shouldUpdateUserWhenUserExistsAndEmailIsNotTaken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = adminService.updateUser(1L, userRequest);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getDisplayName()).isEqualTo("New User");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("updateUser should throw exception when user does not exist")
    void updateUser_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateUser(1L, userRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Käyttäjää ei löydy ID:llä: 1");
    }

    @Test
    @DisplayName("updateUser should throw exception when email is already taken")
    void updateUser_shouldThrowExceptionWhenEmailIsAlreadyTaken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> adminService.updateUser(1L, userRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Sähköposti on jo käytössä");
    }

    @Test
    @DisplayName("updateUser should only update display name when email is null")
    void updateUser_shouldOnlyUpdateDisplayNameWhenEmailIsNull() {
        userRequest.setEmail(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = adminService.updateUser(1L, userRequest);

        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getDisplayName()).isEqualTo("New User");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("deleteUser should delete user when user exists")
    void deleteUser_shouldDeleteUserWhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        adminService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser should throw exception when user does not exist")
    void deleteUser_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> adminService.deleteUser(1L))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Käyttäjää ei löydy ID:llä: 1");
    }

    @Test
    @DisplayName("changeUserEmail should change email when user exists and email is not taken")
    void changeUserEmail_shouldChangeEmailWhenUserExistsAndEmailIsNotTaken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = adminService.changeUserEmail(1L, "new@example.com");

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("changeUserEmail should throw exception when user does not exist")
    void changeUserEmail_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.changeUserEmail(1L, "new@example.com"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Käyttäjää ei löydy ID:llä: 1");
    }

    @Test
    @DisplayName("changeUserEmail should throw exception when email is already taken")
    void changeUserEmail_shouldThrowExceptionWhenEmailIsAlreadyTaken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> adminService.changeUserEmail(1L, "new@example.com"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Sähköposti on jo käytössä");
    }

    @Test
    @DisplayName("getAllRooms should return list of all active rooms")
    void getAllRooms_shouldReturnListOfAllActiveRooms() {
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomResponse> result = adminService.getAllRooms();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(room.getId());
        assertThat(result.get(0).getName()).isEqualTo(room.getName());
    }

    @Test
    @DisplayName("getAllRooms should return empty list when no active rooms exist")
    void getAllRooms_shouldReturnEmptyListWhenNoActiveRoomsExist() {
        room.setIsActive(false);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomResponse> result = adminService.getAllRooms();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("createRoom should create new room with generated ID")
    void createRoom_shouldCreateNewRoomWithGeneratedId() {
        when(roomRepository.existsById(anyString())).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse result = adminService.createRoom(roomRequest);

        assertThat(result.getName()).isEqualTo(roomRequest.getName());
        assertThat(result.getCapacity()).isEqualTo(roomRequest.getCapacity());
        assertThat(result.getDescription()).isEqualTo(roomRequest.getDescription());
        assertThat(result.getLocation()).isEqualTo(roomRequest.getLocation());
        assertThat(result.isActive()).isTrue();
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom should throw exception when room ID already exists")
    void createRoom_shouldThrowExceptionWhenRoomIdAlreadyExists() {
        when(roomRepository.existsById(anyString())).thenReturn(true);

        assertThatThrownBy(() -> adminService.createRoom(roomRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huone ID on jo olemassa");
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
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("updateRoom should throw exception when room does not exist")
    void updateRoom_shouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateRoom("room-12345678", roomRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huonetta ei löydy ID:llä: room-12345678");
    }

    @Test
    @DisplayName("updateRoom should only update provided fields")
    void updateRoom_shouldOnlyUpdateProvidedFields() {
        roomRequest.setName(null);
        roomRequest.setCapacity(null);
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse result = adminService.updateRoom("room-12345678", roomRequest);

        assertThat(result.getName()).isEqualTo(room.getName());
        assertThat(result.getCapacity()).isEqualTo(room.getCapacity());
        assertThat(result.getDescription()).isEqualTo(roomRequest.getDescription());
        assertThat(result.getLocation()).isEqualTo(roomRequest.getLocation());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("deleteRoom should deactivate room when room exists")
    void deleteRoom_shouldDeactivateRoomWhenRoomExists() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        adminService.deleteRoom("room-12345678");

        assertThat(room.getIsActive()).isFalse();
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("deleteRoom should throw exception when room does not exist")
    void deleteRoom_shouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.deleteRoom("room-12345678"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huonetta ei löydy ID:llä: room-12345678");
    }

    @Test
    @DisplayName("changeRoomName should change room name when room exists")
    void changeRoomName_shouldChangeRoomNameWhenRoomExists() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse result = adminService.changeRoomName("room-12345678", "New Room Name");

        assertThat(result.getName()).isEqualTo("New Room Name");
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("changeRoomName should throw exception when room does not exist")
    void changeRoomName_shouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.changeRoomName("room-12345678", "New Room Name"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huonetta ei löydy ID:llä: room-12345678");
    }

    @Test
    @DisplayName("changeRoomCapacity should change room capacity when room exists and capacity is valid")
    void changeRoomCapacity_shouldChangeRoomCapacityWhenRoomExistsAndCapacityIsValid() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse result = adminService.changeRoomCapacity("room-12345678", 15);

        assertThat(result.getCapacity()).isEqualTo(15);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    @DisplayName("changeRoomCapacity should throw exception when room does not exist")
    void changeRoomCapacity_shouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById("room-12345678")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.changeRoomCapacity("room-12345678", 15))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Huonetta ei löydy ID:llä: room-12345678");
    }

    @Test
    @DisplayName("changeRoomCapacity should throw exception when capacity is null")
    void changeRoomCapacity_shouldThrowExceptionWhenCapacityIsNull() {
        assertThatThrownBy(() -> adminService.changeRoomCapacity("room-12345678", null))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Kapasiteetin täytyy olla vähintään 1");
    }

    @Test
    @DisplayName("changeRoomCapacity should throw exception when capacity is less than 1")
    void changeRoomCapacity_shouldThrowExceptionWhenCapacityIsLessThan1() {
        assertThatThrownBy(() -> adminService.changeRoomCapacity("room-12345678", 0))
                .isInstanceOf(ReservationException.class)
                .hasMessage("Kapasiteetin täytyy olla vähintään 1");
    }
}