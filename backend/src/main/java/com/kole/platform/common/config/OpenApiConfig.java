package com.kole.platform.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    OpenAPI koleOpenApi() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("KÓLÉ Platform API")
                    .version("v1")
                    .description(
                        "API for KÓLÉ's circular-economy, "
                            + "waste-collection and impact platform."
                    )
                    .contact(
                        new Contact()
                            .name("KÓLÉ Engineering")
                    )
            )
            .components(
                new Components()
                    .addSecuritySchemes(
                        BEARER_AUTH,
                        new SecurityScheme()
                            .name(BEARER_AUTH)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            );
    }
}