package com.example.backend.controller;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Room reservation endpoints")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    /**
     * Get all reservations for a specific room
     * GET /api/reservations/{roomId}
     */
    @GetMapping("/{roomId}")
    @Operation(summary = "Get reservations by room", description = "Retrieve all reservations for a specific room")
    public ResponseEntity<List<ReservationResponse>> getByRoom(@PathVariable String roomId) {
        List<ReservationResponse> reservations = service.getReservationsByRoom(roomId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Create a new reservation
     * POST /api/reservations
     */
    @PostMapping
    @Operation(summary = "Create reservation", description = "Create a new room reservation")
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse response = service.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing reservation
     * PUT /api/reservations/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update reservation", description = "Update an existing reservation")
    public ResponseEntity<ReservationResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse response = service.updateReservation(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a reservation
     * DELETE /api/reservations/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reservation", description = "Cancel a reservation")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get a single reservation by ID
     * GET /api/reservations/detail/{id}
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "Get reservation by ID", description = "Retrieve a specific reservation")
    public ResponseEntity<ReservationResponse> getById(@PathVariable String id) {
        ReservationResponse response = service.getReservationById(id);
        return ResponseEntity.ok(response);
    }
}