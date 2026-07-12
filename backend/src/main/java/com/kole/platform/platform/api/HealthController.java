package com.kole.platform.platform.api;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "message", "KÓLÉ platform is operational",
                "data", Map.of(
                    "service", "kole-backend",
                    "status", "UP",
                    "timestamp", Instant.now().toString()
                )
            )
        );
    }
}