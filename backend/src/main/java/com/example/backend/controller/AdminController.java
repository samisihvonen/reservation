package com.example.backend.controller;

import com.example.backend.dto.request.EmailChangeRequest;
import com.example.backend.dto.request.RoomCapacityChangeRequest;
import com.example.backend.dto.request.RoomNameChangeRequest;
import com.example.backend.dto.request.RoomRequest;
import com.example.backend.dto.request.UserRequest;
import com.example.backend.dto.response.RoomResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management endpoints")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class AdminController {

    // final ensures the dependency is immutable
    private final AdminService adminService;

    // Constructor injection without Lombok
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ============ USER MANAGEMENT ============

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/email")
    @Operation(summary = "Change user email")
    public ResponseEntity<UserResponse> changeUserEmail(
            @PathVariable Long id,
            @RequestBody EmailChangeRequest request) {
        return ResponseEntity.ok(adminService.changeUserEmail(id, request.getNewEmail()));
    }

    // ============ ROOM MANAGEMENT ============

    @GetMapping("/rooms")
    @Operation(summary = "Get all rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(adminService.getAllRooms());
    }

    @PostMapping("/rooms")
    @Operation(summary = "Create room")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse response = adminService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/rooms/{roomId}")
    @Operation(summary = "Update room")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable String roomId,
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(adminService.updateRoom(roomId, request));
    }

    @DeleteMapping("/rooms/{roomId}")
    @Operation(summary = "Delete room")
    public ResponseEntity<Void> deleteRoom(@PathVariable String roomId) {
        adminService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/rooms/{roomId}/name")
    @Operation(summary = "Change room name")
    public ResponseEntity<RoomResponse> changeRoomName(
            @PathVariable String roomId,
            @RequestBody RoomNameChangeRequest request) {
        return ResponseEntity.ok(adminService.changeRoomName(roomId, request.getNewName()));
    }

    @PatchMapping("/rooms/{roomId}/capacity")
    @Operation(summary = "Change room capacity")
    public ResponseEntity<RoomResponse> changeRoomCapacity(
            @PathVariable String roomId,
            @RequestBody RoomCapacityChangeRequest request) {
        // Fix: Pass the whole request object, not just request.getNewCapacity()
        return ResponseEntity.ok(adminService.changeRoomCapacity(roomId, request));
    }
}