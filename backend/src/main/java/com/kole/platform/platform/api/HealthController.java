package com.kole.platform.platform.api;

import com.kole.platform.common.api.ApiResponse;
import com.kole.platform.common.web.RequestContext;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> healthData = Map.of(
            "service", "kole-backend",
            "status", "UP"
        );

        return ResponseEntity.ok(
            ApiResponse.success(
                "KÓLÉ platform is operational",
                healthData,
                RequestContext.requestId()
            )
        );
    }
}