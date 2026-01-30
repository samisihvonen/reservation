package com.example.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Validointivirheet (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validointivirhe");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
        response.put("message", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Huone on jo varattu
     */
    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ResponseEntity<Map<String, Object>> handleRoomAlreadyBooked(
            RoomAlreadyBookedException ex) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Huone varattu",
                ex.getMessage());
    }

    /**
     * Yleiset varauspoikkeukset
     */
    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<Map<String, Object>> handleReservationException(
            ReservationException ex) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Varausvirhe",
                ex.getMessage());
    }

    @ExceptionHandler(InvalidReservationTimeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTime(
            InvalidReservationTimeException ex) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Virheellinen aika",
                ex.getMessage());
    }

    /**
     * Catch-all fallback
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Palvelinvirhe",
                "Tapahtui odottamaton virhe. Yritä uudelleen.");
    }

    /**
     * Apumetodi
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String error, String message) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);

        return ResponseEntity.status(status).body(response);
    }
}
