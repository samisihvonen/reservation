// ============================================
// ReservationResponse DTO
// ============================================
// File: src/main/java/com/example/backend/dto/ReservationResponse.java

package com.example.backend.dto;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class ReservationResponse {
    
    private String id;
    private String roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ReservationResponse() {
    }

    public ReservationResponse(String id, String roomId, LocalDateTime startTime,
            LocalDateTime endTime, String user, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() {
        return id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ReservationResponse{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", user='" + user + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}