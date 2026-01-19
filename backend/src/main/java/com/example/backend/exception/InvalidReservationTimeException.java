// File: src/main/java/com/example/backend/exception/InvalidReservationTimeException.java

package com.example.backend.exception;

public class InvalidReservationTimeException extends ReservationException {
    public InvalidReservationTimeException(String message) {
        super(message);
    }
}