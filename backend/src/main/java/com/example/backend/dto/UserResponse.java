// File 1: src/main/java/com/example/backend/dto/UserResponse.java
package com.example.backend.dto;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String email;
    private String displayName;
    private LocalDateTime createdAt;

    public UserResponse(Long id, String email, String displayName, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}