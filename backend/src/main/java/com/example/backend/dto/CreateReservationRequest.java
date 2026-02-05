// ============================================
// CreateReservationRequest DTO
// ============================================
// File: src/main/java/com/example/backend/dto/CreateReservationRequest.java

package com.example.backend.dto;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class CreateReservationRequest {
    
    @NotBlank(message = "Huoneen ID on pakollinen")
    private String roomId;
    
    @NotNull(message = "Alkamisaika on pakollinen")
    private LocalDateTime startTime;
    
    @NotNull(message = "Päättymisaika on pakollinen")
    private LocalDateTime endTime;
    
    @NotBlank(message = "Käyttäjän nimi on pakollinen")
    private String user;

    // Constructors
    public CreateReservationRequest() {
    }

    public CreateReservationRequest(String roomId, LocalDateTime startTime, 
            LocalDateTime endTime, String user) {
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
    }

    // Getters
    public String getRoomId() {
        return roomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getUser() {
        return user;
    }

    // Setters
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "CreateReservationRequest{" +
                "roomId='" + roomId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", user='" + user + '\'' +
                '}';
    }
}