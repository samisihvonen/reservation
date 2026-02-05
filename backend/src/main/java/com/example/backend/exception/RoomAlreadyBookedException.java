// File: src/main/java/com/example/backend/exception/RoomAlreadyBookedException.java

package com.example.backend.exception;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
public class RoomAlreadyBookedException extends ReservationException {
    public RoomAlreadyBookedException(String message) {
        super(message);
    }
}