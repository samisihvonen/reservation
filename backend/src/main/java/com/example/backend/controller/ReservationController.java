package com.example.backend.controller;

import com.example.backend.model.Reservation;
import com.example.backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository repository;

    // Haetaan tietyn huoneen varaukset (Reactin useEffect käyttää tätä)
    @GetMapping("/{roomId}")
    public List<Reservation> getByRoom(@PathVariable String roomId) {
        return repository.findByRoomId(roomId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Reservation req) {
        // Estetään menneisyyteen varaaminen (hyvä lisä validointiin)
        if (req.getStartTime().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Varaus ei voi olla menneisyydessä.");
        }

        List<Reservation> existing = repository.findByRoomId(req.getRoomId());
        
        // Päällekkäisyyden tarkistuslogiikka
        boolean overlap = existing.stream().anyMatch(r -> 
            req.getStartTime().isBefore(r.getEndTime()) && 
            req.getEndTime().isAfter(r.getStartTime())
        );

        if (overlap) {
            return ResponseEntity.status(409).body("Huone on jo varattu valittuna aikana!");
        }

        return ResponseEntity.ok(repository.save(req));
    }
}