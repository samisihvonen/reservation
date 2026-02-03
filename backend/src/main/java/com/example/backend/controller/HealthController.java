package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}