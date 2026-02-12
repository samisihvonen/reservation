package com.example.backend.repository;

import com.example.backend.model.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

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