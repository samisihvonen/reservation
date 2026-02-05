// ============================================
// ROOM REPOSITORY
// ============================================
// File: src/main/java/com/example/backend/repository/RoomRepository.java

package com.example.backend.repository;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.backend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    
    /**
     * Haetaan kaikki aktiiviset huoneet
     */
    List<Room> findByIsActiveTrue();
    
    /**
     * Haetaan huone nimen perusteella
     */
    Room findByName(String name);
    
    /**
     * Tarkistetaan, onko huone olemassa
     */
    boolean existsByName(String name);
}