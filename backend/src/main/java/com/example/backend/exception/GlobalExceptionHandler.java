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
         * Validation errors (@Valid)
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationException(
                        MethodArgumentNotValidException ex) {

                Map<String, Object> response = new HashMap<>();
                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("error", "Validation error");

                Map<String, String> fieldErrors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
                response.put("message", fieldErrors);

                return ResponseEntity.badRequest().body(response);
        }

        /**
         * Room is already booked
         */
        @ExceptionHandler(RoomAlreadyBookedException.class)
        public ResponseEntity<Map<String, Object>> handleRoomAlreadyBooked(
                        RoomAlreadyBookedException ex) {

                return buildErrorResponse(
                                HttpStatus.CONFLICT,
                                "Room already booked",
                                ex.getMessage());
        }

        /**
         * General reservation exceptions
         */
        @ExceptionHandler(ReservationException.class)
        public ResponseEntity<Map<String, Object>> handleReservationException(
                        ReservationException ex) {

                return buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "Reservation error",
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidReservationTimeException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidTime(
                        InvalidReservationTimeException ex) {

                return buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "Invalid time",
                                ex.getMessage());
        }

        /**
         * Catch-all fallback
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

                return buildErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Server error",
                                "An unexpected error occurred. Please try again.");
        }

        /**
         * Helper method to build a standard error response
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
