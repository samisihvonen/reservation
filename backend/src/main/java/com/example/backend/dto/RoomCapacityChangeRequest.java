// File: src/main/java/com/example/backend/dto/RoomCapacityChangeRequest.java

package com.example.backend.dto;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RoomCapacityChangeRequest {

    @NotNull(message = "Uusi kapasiteetti on pakollinen")
    @Min(value = 1, message = "Kapasiteetin täytyy olla vähintään 1")
    private Integer newCapacity;

    public Integer getNewCapacity() { return newCapacity; }
    public void setNewCapacity(Integer newCapacity) { this.newCapacity = newCapacity; }
}