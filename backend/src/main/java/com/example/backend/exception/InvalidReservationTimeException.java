// File: src/main/java/com/example/backend/exception/InvalidReservationTimeException.java

package com.example.backend.exception;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
public class InvalidReservationTimeException extends ReservationException {
    public InvalidReservationTimeException(String message) {
        super(message);
    }
}