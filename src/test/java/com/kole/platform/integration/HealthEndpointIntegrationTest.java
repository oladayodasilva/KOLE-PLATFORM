package com.kole.platform.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@org.springframework.boot.test.context.SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT
)
class HealthEndpointIntegrationTest
    extends IntegrationTestSupport {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthEndpointReturnsSuccess() {
        ResponseEntity<String> response =
            restTemplate.getForEntity(
                "/api/v1/health",
                String.class
            );

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
            .contains("\"success\":true")
            .contains("\"status\":\"UP\"");

        assertThat(
            response.getHeaders().getFirst("X-Request-ID")
        ).isNotBlank();
    }
}