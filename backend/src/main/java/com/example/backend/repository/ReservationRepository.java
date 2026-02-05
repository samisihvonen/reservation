package com.example.backend.repository;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByRoomId(String roomId);
}