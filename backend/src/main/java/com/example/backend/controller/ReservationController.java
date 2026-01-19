// ============================================
// 7. UPDATED CONTROLLER (Use Service)
// ============================================
// File: src/main/java/com/example/backend/controller/ReservationController.java

package com.example.backend.controller;

import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.dto.ReservationResponse;
import com.example.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    
    @Autowired
    private ReservationService service;
    
    /**
     * Haetaan kaikki varaukset tietylle huoneelle
     * GET /api/reservations/{roomId}
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ReservationResponse>> getByRoom(@PathVariable String roomId) {
        List<ReservationResponse> reservations = service.getReservationsByRoom(roomId);
        return ResponseEntity.ok(reservations);
    }
    
    /**
     * Luodaan uusi varaus
     * POST /api/reservations
     * Body: { "roomId": "room-1", "startTime": "...", "endTime": "...", "user": "..." }
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse response = service.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Poistetaan varaus
     * DELETE /api/reservations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}