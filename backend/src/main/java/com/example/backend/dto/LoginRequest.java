// ============================================
// 5. Auth Request DTOs
// ============================================
// File: src/main/java/com/example/backend/dto/LoginRequest.java

package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    
    @NotBlank(message = "Sähköposti on pakollinen")
    @Email(message = "Kelvollinen sähköpostiosoite vaaditaan")
    private String email;
    
    @NotBlank(message = "Salasana on pakollinen")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}