// ============================================
// 2. RESPONSE DTO
// ============================================
// File: src/main/java/com/example/backend/dto/ReservationResponse.java

package com.example.backend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    
    private String id;
    private String roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}