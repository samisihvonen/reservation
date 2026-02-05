// ============================================
// 4. CUSTOM EXCEPTION CLASSES
// ============================================
// File: src/main/java/com/example/backend/exception/ReservationException.java

package com.example.backend.exception;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
public class ReservationException extends RuntimeException {
    public ReservationException(String message) {
        super(message);
    }
}
