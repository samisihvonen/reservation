package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

@RestController
public class HealthController {
    private final Logger log = LoggerFactory.getLogger(HealthController.class);
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        String timestamp = Instant.now().toString();
        log.info("Health check initiated at {}", timestamp);
        return ResponseEntity.ok("Health check: OK");
    }
}