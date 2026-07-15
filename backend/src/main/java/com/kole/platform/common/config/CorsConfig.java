package com.kole.platform.common.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource(
        @Value(
            "${kole.cors.allowed-origins:"
                + "http://localhost:3000}"
        )
        List<String> allowedOrigins
    ) {
        CorsConfiguration configuration =
            new CorsConfiguration();

        configuration.setAllowedOrigins(
            allowedOrigins
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type",
                "X-Request-ID"
            )
        );

        configuration.setExposedHeaders(
            List.of("X-Request-ID")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/api/**",
            configuration
        );

        return source;
    }
}