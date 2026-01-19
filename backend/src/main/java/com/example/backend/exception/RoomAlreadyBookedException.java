// File: src/main/java/com/example/backend/exception/RoomAlreadyBookedException.java

package com.example.backend.exception;

public class RoomAlreadyBookedException extends ReservationException {
    public RoomAlreadyBookedException(String message) {
        super(message);
    }
}