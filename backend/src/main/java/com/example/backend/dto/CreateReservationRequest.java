// ============================================
// 1. CREATE REQUEST DTO
// ============================================
// File: src/main/java/com/example/backend/dto/CreateReservationRequest.java

package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    
    @NotBlank(message = "Huoneen ID on pakollinen")
    private String roomId;
    
    @NotNull(message = "Alkamisaika on pakollinen")
    private LocalDateTime startTime;
    
    @NotNull(message = "Päättymisaika on pakollinen")
    private LocalDateTime endTime;
    
    @NotBlank(message = "Käyttäjän nimi on pakollinen")
    private String user;
}