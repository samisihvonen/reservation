// File 3: src/main/java/com/example/backend/dto/RoomResponse.java
package com.example.backend.dto;

import java.time.LocalDateTime;

public class RoomResponse {
    private String id;
    private String name;
    private Integer capacity;
    private String description;
    private String location;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomResponse(String id, String name, Integer capacity, String description,
                       String location, Boolean isActive, LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.description = description;
        this.location = location;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Integer getCapacity() { return capacity; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Boolean getIsActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}