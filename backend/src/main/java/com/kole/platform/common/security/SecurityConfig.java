package com.kole.platform.common.security;

import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint
        authenticationEntryPoint;

    private final RestAccessDeniedHandler
        accessDeniedHandler;

    public SecurityConfig(
        RestAuthenticationEntryPoint
            authenticationEntryPoint,
        RestAccessDeniedHandler
            accessDeniedHandler
    ) {
        this.authenticationEntryPoint =
            authenticationEntryPoint;
        this.accessDeniedHandler =
            accessDeniedHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http
    ) throws Exception {

        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/health",
                    "/api/v1/auth/register",
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/logout",
                    "/actuator/health",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint(
                    authenticationEntryPoint
                )
                .accessDeniedHandler(
                    accessDeniedHandler
                )
            )
            .oauth2ResourceServer(resourceServer ->
                resourceServer
                    .jwt(jwt ->
                        jwt.jwtAuthenticationConverter(
                            jwtAuthenticationConverter()
                        )
                    )
                    .authenticationEntryPoint(
                        authenticationEntryPoint
                    )
                    .accessDeniedHandler(
                        accessDeniedHandler
                    )
            )
            .build();
    }

    @Bean
    JwtAuthenticationConverter
        jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
            new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
            new RolesClaimConverter()
        );

        return converter;
    }

    private static class RolesClaimConverter
        implements Converter<
            Jwt,
            Collection<GrantedAuthority>
        > {

        @Override
        public Collection<GrantedAuthority> convert(
            Jwt jwt
        ) {
            List<String> roles =
                jwt.getClaimAsStringList("roles");

            if (roles == null) {
                return List.of();
            }

            return roles
                .stream()
                .map(role ->
                    new SimpleGrantedAuthority(
                        "ROLE_" + role
                    )
                )
                .map(GrantedAuthority.class::cast)
                .toList();
        }
    }
}