// ============================================
// 4. CUSTOM EXCEPTION CLASSES
// ============================================
// File: src/main/java/com/example/backend/exception/ReservationException.java

package com.example.backend.exception;

public class ReservationException extends RuntimeException {
    public ReservationException(String message) {
        super(message);
    }
}
