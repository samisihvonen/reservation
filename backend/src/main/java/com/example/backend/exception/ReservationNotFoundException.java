package com.example.backend.exception;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
        super(message);
    }

    public ReservationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
