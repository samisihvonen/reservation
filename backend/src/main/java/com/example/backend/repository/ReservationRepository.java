package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByRoomId(String roomId);
}