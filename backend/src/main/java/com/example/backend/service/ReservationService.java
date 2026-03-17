package com.example.backend.service;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.exception.InvalidReservationTimeException;
import com.example.backend.exception.ReservationException;
import com.example.backend.exception.RoomAlreadyBookedException;
import com.example.backend.model.Reservation;
import com.example.backend.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all reservations for a specific room
     */
    public List<ReservationResponse> getReservationsByRoom(String roomId) {
        return repository.findByRoomId(roomId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single reservation by ID
     */
    public ReservationResponse getReservationById(String id) {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationException("Reservation not found with ID: " + id));
        return toResponse(reservation);
    }

    /**
     * Create a new reservation with validation
     */
    public ReservationResponse createReservation(CreateReservationRequest request) {
        LocalDateTime now = LocalDateTime.now();
        // 1. Validate that start time is in the future
        if (request.getStartTime().isBefore(now)) {
            throw new InvalidReservationTimeException(
                    "Reservation cannot be in the past.");
        }

        // 2. Validate that end time is after start time
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new InvalidReservationTimeException(
                    "End time cannot be before start time.");
        }

        // 3. Check for overlapping reservations
        List<Reservation> existingReservations = repository.findByRoomId(request.getRoomId());
        boolean hasOverlap = existingReservations.stream()
                .anyMatch(r -> isOverlapping(request, r));

        if (hasOverlap) {
            throw new RoomAlreadyBookedException(
                    "Room is already booked for the selected time.");
        }

        // 4. Save the new reservation
        Reservation reservation = new Reservation();
        reservation.setRoomId(request.getRoomId());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setUser(request.getUser());

        Reservation saved = repository.save(reservation);
        return toResponse(saved);
    }

    /**
     * Update an existing reservation
     */
    public ReservationResponse updateReservation(String id, CreateReservationRequest request) {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationException("Reservation not found with ID: " + id));
        LocalDateTime now = LocalDateTime.now();
        // Validate the new time
        if (request.getStartTime().isBefore(now)) {
            throw new InvalidReservationTimeException(
                    "Reservation start time cannot be in the past.");
        }

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new InvalidReservationTimeException(
                    "End time cannot be before start time.");
        }

        // Check for overlapping reservations (excluding this reservation)
        List<Reservation> existingReservations = repository.findByRoomId(request.getRoomId());
        boolean hasOverlap = existingReservations.stream()
                .filter(r -> !r.getId().equals(id))
                .anyMatch(r -> isOverlapping(request, r));

        if (hasOverlap) {
            throw new RoomAlreadyBookedException(
                    "Room is already booked for the new time slot.");
        }

        // Update the reservation
        reservation.setRoomId(request.getRoomId());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setUser(request.getUser());

        Reservation updated = repository.save(reservation);
        return toResponse(updated);
    }

    /**
     * Delete a reservation by ID
     */
    public void deleteReservation(String id) {
        if (!repository.existsById(id)) {
            throw new ReservationException("Reservation not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Check whether two reservations overlap
     */
    private boolean isOverlapping(CreateReservationRequest request, Reservation existing) {
        return request.getStartTime().isBefore(existing.getEndTime())
                && request.getEndTime().isAfter(existing.getStartTime());
    }

    /**
     * Convert a Reservation entity to a ReservationResponse DTO
     */
    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRoomId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getUser(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt());
    }
}