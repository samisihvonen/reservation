package com.example.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import java.util.Date;

@RestController
public class HealthController {
    private final Logger log = LoggerFactory.getLogger(HealthController.class);
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        String logMessage = "Health check initiated at: " + new Date();
        log.info(logMessage);
        return ResponseEntity.ok("Health check: OK");
    }
}