// ============================================
// ADMIN SERVICE - User & Room Management
// ============================================
// File: src/main/java/com/example/backend/service/AdminService.java

package com.example.backend.service;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.backend.dto.UserResponse;
import com.example.backend.dto.UserRequest;
import com.example.backend.dto.RoomRequest;
import com.example.backend.dto.RoomResponse;
import com.example.backend.exception.ReservationException;
import com.example.backend.model.User;
import com.example.backend.model.Room;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    // ============ USER MANAGEMENT ============

    /**
     * Haetaan kaikki käyttäjät
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Haetaan käyttäjä ID:n perusteella
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ReservationException("Käyttäjää ei löydy ID:llä: " + id));
        return toUserResponse(user);
    }

    /**
     * Päivitetään käyttäjän tietoja
     */
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ReservationException("Käyttäjää ei löydy ID:llä: " + id));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }

        if (request.getEmail() != null && !user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ReservationException("Sähköposti on jo käytössä");
            }
            user.setEmail(request.getEmail());
        }

        User updated = userRepository.save(user);
        return toUserResponse(updated);
    }

    /**
     * Poistetaan käyttäjä
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ReservationException("Käyttäjää ei löydy ID:llä: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Muutetaan käyttäjän sähköposti
     */
    public UserResponse changeUserEmail(Long id, String newEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ReservationException("Käyttäjää ei löydy ID:llä: " + id));

        if (userRepository.existsByEmail(newEmail)) {
            throw new ReservationException("Sähköposti on jo käytössä");
        }

        user.setEmail(newEmail);
        User updated = userRepository.save(user);
        return toUserResponse(updated);
    }

    // ============ ROOM MANAGEMENT ============

    /**
     * Haetaan kaikki huoneet
     */
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(Room::getIsActive)
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    /**
     * Luodaan uusi huone
     */
    public RoomResponse createRoom(RoomRequest request) {
        String roomId = "room-" + UUID.randomUUID().toString().substring(0, 8);

        if (roomRepository.existsById(roomId)) {
            throw new ReservationException("Huone ID on jo olemassa");
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
     * Päivitetään huoneen tietoja
     */
    public RoomResponse updateRoom(String roomId, RoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException("Huonetta ei löydy ID:llä: " + roomId));

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
     * Poistetaan huone
     */
    public void deleteRoom(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException("Huonetta ei löydy ID:llä: " + roomId));

        room.setIsActive(false);
        roomRepository.save(room);
    }

    /**
     * Muutetaan huoneen nimeä
     */
    public RoomResponse changeRoomName(String roomId, String newName) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException("Huonetta ei löydy ID:llä: " + roomId));

        room.setName(newName);
        Room updated = roomRepository.save(room);
        return toRoomResponse(updated);
    }

    /**
     * Muutetaan huoneen kapasiteettia
     */
    public RoomResponse changeRoomCapacity(String roomId, Integer newCapacity) {
        if (newCapacity == null || newCapacity < 1) {
            throw new ReservationException("Kapasiteetin täytyy olla vähintään 1");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ReservationException("Huonetta ei löydy ID:llä: " + roomId));

        room.setCapacity(newCapacity);
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
