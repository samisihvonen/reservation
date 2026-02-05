// File: src/main/java/com/example/backend/dto/UserRequest.java

package com.example.backend.dto;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequest {

    @NotBlank(message = "Sähköposti on pakollinen")
    @Email(message = "Kelvollinen sähköpostiosoite vaaditaan")
    private String email;

    @NotBlank(message = "Nimi on pakollinen")
    @Size(min = 2, max = 100)
    private String displayName;

    public UserRequest() {}

    public UserRequest(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}