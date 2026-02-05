// File: src/main/java/com/example/backend/dto/RoomNameChangeRequest.java

package com.example.backend.dto;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoomNameChangeRequest {

    @NotBlank(message = "Uusi nimi on pakollinen")
    @Size(min = 1, max = 100)
    private String newName;

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}