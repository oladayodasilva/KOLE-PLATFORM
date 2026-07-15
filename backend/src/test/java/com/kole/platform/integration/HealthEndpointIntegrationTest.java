package com.kole.platform.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HealthEndpointIntegrationTest
    extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Test
    void healthEndpointReturnsSuccess() {
        RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .build();

        ResponseEntity<String> response = restClient
            .get()
            .uri("/api/v1/health")
            .retrieve()
            .toEntity(String.class);

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