// ============================================
// EXTENDED RESERVATION SERVICE
// ============================================
// File: src/main/java/com/example/backend/service/ReservationService.java

package com.example.backend.service;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.backend.dto.CreateReservationRequest;
import com.example.backend.dto.ReservationResponse;
import com.example.backend.exception.InvalidReservationTimeException;
import com.example.backend.exception.ReservationException;
import com.example.backend.exception.RoomAlreadyBookedException;
import com.example.backend.model.Reservation;
import com.example.backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repository;

    /**
     * Haetaan kaikki varaukset tietylle huoneelle
     */
    public List<ReservationResponse> getReservationsByRoom(String roomId) {
        return repository.findByRoomId(roomId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Haetaan yksittäinen varaus ID:n perusteella
     */
    public ReservationResponse getReservationById(String id) {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationException("Varausta ei löydy ID:llä: " + id));
        return toResponse(reservation);
    }

    /**
     * Luodaan uusi varaus validoinnin kanssa
     */
    public ReservationResponse createReservation(CreateReservationRequest request) {
        LocalDateTime now = LocalDateTime.now();
        // 1. Validoi että alkamisaika on tulevaisuudessa
        if (request.getStartTime().isBefore(now)) {
            throw new InvalidReservationTimeException(
                    "Varaus ei voi olla menneisyydessä.");
        }

        // 2. Validoi että päättymisaika on alkamisajan jälkeen
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new InvalidReservationTimeException(
                    "Päättymisaika ei voi olla alkamisaikaa ennen.");
        }

        // 3. Tarkista päällekkäisyydet
        List<Reservation> existingReservations = repository.findByRoomId(request.getRoomId());
        boolean hasOverlap = existingReservations.stream()
                .anyMatch(r -> isOverlapping(request, r));

        if (hasOverlap) {
            throw new RoomAlreadyBookedException(
                    "Huone on jo varattu valittuna aikana.");
        }

        // 4. Tallenna uusi varaus
        Reservation reservation = new Reservation();
        reservation.setRoomId(request.getRoomId());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setUser(request.getUser());

        Reservation saved = repository.save(reservation);
        return toResponse(saved);
    }

    /**
     * Päivitetään olemassa olevaa varausta
     */
    public ReservationResponse updateReservation(String id, CreateReservationRequest request) {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationException("Varausta ei löydy ID:llä: " + id));
        LocalDateTime now = LocalDateTime.now();
        // Validoi uuden ajan
        if (request.getStartTime().isBefore(now)) {
            throw new InvalidReservationTimeException(
                    "Varauksen alkamisaika ei voi olla menneisyydessä.");
        }

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new InvalidReservationTimeException(
                    "Päättymisaika ei voi olla alkamisaikaa ennen.");
        }

        // Tarkista päällekkäisyydet (poislukien tämä varaus)
        List<Reservation> existingReservations = repository.findByRoomId(request.getRoomId());
        boolean hasOverlap = existingReservations.stream()
                .filter(r -> !r.getId().equals(id))
                .anyMatch(r -> isOverlapping(request, r));

        if (hasOverlap) {
            throw new RoomAlreadyBookedException(
                    "Huone on varattu uuden ajan osalta.");
        }

        // Päivitä varaus
        reservation.setRoomId(request.getRoomId());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setUser(request.getUser());

        Reservation updated = repository.save(reservation);
        return toResponse(updated);
    }

    /**
     * Poistaa varauksen ID:n perusteella
     */
    public void deleteReservation(String id) {
        if (!repository.existsById(id)) {
            throw new ReservationException("Varausta ei löydy ID:llä: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Tarkista, ovatko kaksi varausta päällekkäisiä
     */
    private boolean isOverlapping(CreateReservationRequest request, Reservation existing) {
        return request.getStartTime().isBefore(existing.getEndTime())
                && request.getEndTime().isAfter(existing.getStartTime());
    }

    /**
     * Muuntaa Reservation-entityn Response-DTO:ksi
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