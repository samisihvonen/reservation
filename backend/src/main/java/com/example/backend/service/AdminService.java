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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public AdminService(UserRepository userRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    // ============ USER MANAGEMENT ============

    /**
     * Get all users
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ReservationException("User not found with ID: " + id));
        return toUserResponse(user);
    }

    /**
     * Update user details
     */
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ReservationException("User not found with ID: " + id));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }

        if (request.getEmail() != null && !user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ReservationException("Email is already in use");
            }
            user.setEmail(request.getEmail());
        }

        User updated = userRepository.save(user);
        return toUserResponse(updated);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ReservationException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Change user email address
     */
    public UserResponse changeUserEmail(Long id, String newEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ReservationException("User not found with ID: " + id));

        if (userRepository.existsByEmail(newEmail)) {
            throw new ReservationException("Email is already in use");
        }

        user.setEmail(newEmail);
        User updated = userRepository.save(user);
        return toUserResponse(updated);
    }

    // ============ ROOM MANAGEMENT ============

    /**
     * Get all active rooms
     */
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(Room::getIsActive)
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a new room
     */
    public RoomResponse createRoom(RoomRequest request) {
        String roomId = "room-" + UUID.randomUUID().toString().substring(0, 8);

        if (roomRepository.existsById(roomId)) {
            throw new ReservationException("Room ID already exists");
        }

        Room room = new Room();
        room.setId(roomId);
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        room.setDescription(request.getDescription());
        room.setLocation(request.getLocation());
        room.setIsActive(true);

        Room saved = roomRepository.save(room);
        return toRoomResponse(saved);
    }

    /**
     * Update room details
     */
    public RoomResponse updateRoom(String roomId, RoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException("Room not found with ID: " + roomId));

        if (request.getName() != null) {
            room.setName(request.getName());
        }

        if (request.getCapacity() != null) {
            room.setCapacity(request.getCapacity());
        }

        if (request.getDescription() != null) {
            room.setDescription(request.getDescription());
        }

        if (request.getLocation() != null) {
            room.setLocation(request.getLocation());
        }

        Room updated = roomRepository.save(room);
        return toRoomResponse(updated);
    }

    /**
     * Soft-delete a room (marks as inactive)
     */
    public void deleteRoom(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException("Room not found with ID: " + roomId));

        room.setIsActive(false);
        roomRepository.save(room);
    }

    /**
     * Change room name
     */
    public RoomResponse changeRoomName(String roomId, String newName) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException("Room not found with ID: " + roomId));

        room.setName(newName);
        Room updated = roomRepository.save(room);
        return toRoomResponse(updated);
    }

    /**
     * Change room capacity
     */
    public RoomResponse changeRoomCapacity(String roomId, RoomCapacityChangeRequest request) {
        if (request == null || request.getNewCapacity() < 1) {
            throw new ReservationException("Capacity must be at least 1");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException("Room not found with ID: " + roomId));

        room.setCapacity(request.getNewCapacity());
        Room updated = roomRepository.save(room);
        return toRoomResponse(updated);
    }

    // ============ HELPER METHODS ============

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt());
    }

    private RoomResponse toRoomResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getDescription(),
                room.getLocation(),
                room.getIsActive(),
                room.getCreatedAt(),
                room.getUpdatedAt());
    }
}