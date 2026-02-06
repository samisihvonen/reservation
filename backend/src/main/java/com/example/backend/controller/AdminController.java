// ============================================
// ADMIN CONTROLLER - User & Room Management
// ============================================
// File: src/main/java/com/example/backend/controller/AdminController.java

package com.example.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.backend.dto.UserResponse;
import com.example.backend.dto.UserRequest;
import com.example.backend.dto.EmailChangeRequest;
import com.example.backend.dto.RoomRequest;
import com.example.backend.dto.RoomResponse;
import com.example.backend.dto.RoomNameChangeRequest;
import com.example.backend.dto.RoomCapacityChangeRequest;
import com.example.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ============ USER MANAGEMENT ============

    /**
     * Haetaan kaikki käyttäjät
     * GET /api/admin/users
     */
    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve list of all users (Admin only)")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Haetaan yksittäinen käyttäjä ID:n perusteella
     * GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Päivitetään käyttäjän tietoja
     * PUT /api/admin/users/{id}
     */
    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update user information (Admin only)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        UserResponse user = adminService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    /**
     * Poistetaan käyttäjä
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Remove a user from the system (Admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Muutetaan käyttäjän sähköposti
     * PATCH /api/admin/users/{id}/email
     */
    @PatchMapping("/users/{id}/email")
    @Operation(summary = "Change user email", description = "Update user's email address")
    public ResponseEntity<UserResponse> changeUserEmail(
            @PathVariable Long id,
            @RequestBody EmailChangeRequest request) {
        UserResponse user = adminService.changeUserEmail(id, request.getNewEmail());
        return ResponseEntity.ok(user);
    }

    // ============ ROOM MANAGEMENT ============

    /**
     * Haetaan kaikki huoneet
     * GET /api/admin/rooms
     */
    @GetMapping("/rooms")
    @Operation(summary = "Get all rooms", description = "Retrieve list of all conference rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = adminService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Luodaan uusi huone
     * POST /api/admin/rooms
     */
    @PostMapping("/rooms")
    @Operation(summary = "Create room", description = "Add a new conference room (Admin only)")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse room = adminService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    /**
     * Päivitetään huoneen tietoja
     * PUT /api/admin/rooms/{roomId}
     */
    @PutMapping("/rooms/{roomId}")
    @Operation(summary = "Update room", description = "Modify room information (Admin only)")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable String roomId,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse room = adminService.updateRoom(roomId, request);
        return ResponseEntity.ok(room);
    }

    /**
     * Poistetaan huone
     * DELETE /api/admin/rooms/{roomId}
     */
    @DeleteMapping("/rooms/{roomId}")
    @Operation(summary = "Delete room", description = "Remove a conference room (Admin only)")
    public ResponseEntity<Void> deleteRoom(@PathVariable String roomId) {
        adminService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Muutetaan huoneen nimeä
     * PATCH /api/admin/rooms/{roomId}/name
     */
    @PatchMapping("/rooms/{roomId}/name")
    @Operation(summary = "Change room name", description = "Update room name")
    public ResponseEntity<RoomResponse> changeRoomName(
            @PathVariable String roomId,
            @RequestBody RoomNameChangeRequest request) {
        RoomResponse room = adminService.changeRoomName(roomId, request.getNewName());
        return ResponseEntity.ok(room);
    }

    /**
     * Muutetaan huoneen kapasiteettia
     * PATCH /api/admin/rooms/{roomId}/capacity
     */
    @PatchMapping("/rooms/{roomId}/capacity")
    @Operation(summary = "Change room capacity", description = "Update room capacity")
    public ResponseEntity<RoomResponse> changeRoomCapacity(
            @PathVariable String roomId,
            @RequestBody RoomCapacityChangeRequest request) {
        RoomResponse room = adminService.changeRoomCapacity(roomId, request.getNewCapacity());
        return ResponseEntity.ok(room);
    }
}
