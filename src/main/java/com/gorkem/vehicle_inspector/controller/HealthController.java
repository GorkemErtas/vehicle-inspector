package com.gorkem.vehicle_inspector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = Map.of(
                "status", "UP",
                "application", "Vehicle Inspector API",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}
