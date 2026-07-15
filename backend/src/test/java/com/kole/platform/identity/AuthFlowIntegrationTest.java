package com.kole.platform.identity;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import com.kole.platform.integration.IntegrationTestSupport;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthFlowIntegrationTest extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void userCanRegisterLoginRefreshAndAccessProfile()
        throws Exception {

        RestClient restClient = restClient();

        String uniqueEmail =
            "user-" + System.nanoTime() + "@example.com";

        Map<String, String> registration = Map.of(
            "fullName", "Test Household",
            "email", uniqueEmail,
            "phoneNumber", uniquePhoneNumber(),
            "password", "SecurePassword123"
        );

        ResponseEntity<String> registerResponse =
            restClient.post()
                .uri("/api/v1/auth/register")
                .body(registration)
                .retrieve()
                .toEntity(String.class);

        assertThat(registerResponse.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);

        JsonNode registrationBody =
            jsonMapper.readTree(registerResponse.getBody());

        String accessToken =
            registrationBody
                .path("data")
                .path("accessToken")
                .asText();

        String refreshToken =
            registrationBody
                .path("data")
                .path("refreshToken")
                .asText();

        assertThat(accessToken).isNotBlank();
        assertThat(refreshToken).isNotBlank();

        ResponseEntity<String> profileResponse =
            restClient.get()
                .uri("/api/v1/users/me")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + accessToken
                )
                .retrieve()
                .toEntity(String.class);

        assertThat(profileResponse.getStatusCode())
            .isEqualTo(HttpStatus.OK);

        assertThat(profileResponse.getBody())
            .contains(uniqueEmail)
            .contains("HOUSEHOLD");

        Map<String, String> refreshRequest = Map.of(
            "refreshToken", refreshToken
        );

        ResponseEntity<String> refreshResponse =
            restClient.post()
                .uri("/api/v1/auth/refresh")
                .body(refreshRequest)
                .retrieve()
                .toEntity(String.class);

        assertThat(refreshResponse.getStatusCode())
            .isEqualTo(HttpStatus.OK);

        JsonNode refreshBody =
            jsonMapper.readTree(refreshResponse.getBody());

        String rotatedRefreshToken =
            refreshBody
                .path("data")
                .path("refreshToken")
                .asText();

        assertThat(rotatedRefreshToken)
            .isNotBlank()
            .isNotEqualTo(refreshToken);

        ResponseEntity<String> reusedRefreshTokenResponse =
            restClient.post()
                .uri("/api/v1/auth/refresh")
                .body(refreshRequest)
                .exchange((request, response) ->
                    ResponseEntity.status(response.getStatusCode())
                        .headers(response.getHeaders())
                        .body(
                            response.getBody()
                                .readAllBytes()
                                .length == 0
                                    ? ""
                                    : new String(
                                        response.getBody()
                                            .readAllBytes()
                                    )
                        )
                );

        assertThat(
            reusedRefreshTokenResponse.getStatusCode()
        ).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void duplicateEmailIsRejected() {
        RestClient restClient = restClient();

        String uniqueEmail =
            "duplicate-" + System.nanoTime()
                + "@example.com";

        Map<String, String> firstRegistration = Map.of(
            "fullName", "First User",
            "email", uniqueEmail,
            "phoneNumber", uniquePhoneNumber(),
            "password", "SecurePassword123"
        );

        Map<String, String> secondRegistration = Map.of(
            "fullName", "Second User",
            "email", uniqueEmail,
            "phoneNumber", uniquePhoneNumber(),
            "password", "SecurePassword123"
        );

        restClient.post()
            .uri("/api/v1/auth/register")
            .body(firstRegistration)
            .retrieve()
            .toBodilessEntity();

        ResponseEntity<String> duplicateResponse =
            restClient.post()
                .uri("/api/v1/auth/register")
                .body(secondRegistration)
                .exchange((request, response) ->
                    ResponseEntity.status(response.getStatusCode())
                        .headers(response.getHeaders())
                        .body("")
                );

        assertThat(duplicateResponse.getStatusCode())
            .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void protectedEndpointRejectsMissingToken() {
        RestClient restClient = restClient();

        ResponseEntity<String> response =
            restClient.get()
                .uri("/api/v1/users/me")
                .exchange((request, serverResponse) ->
                    ResponseEntity
                        .status(serverResponse.getStatusCode())
                        .headers(serverResponse.getHeaders())
                        .body("")
                );

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private RestClient restClient() {
        return RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .build();
    }

    private String uniquePhoneNumber() {
        String digits = Long
            .toString(System.nanoTime())
            .replace("-", "");

        String suffix = digits.substring(
            Math.max(0, digits.length() - 9)
        );

        return "+2348" + suffix;
    }
}