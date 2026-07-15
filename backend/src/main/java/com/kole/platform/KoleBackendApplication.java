package com.kole.platform;

import com.kole.platform.identity.application.AdminBootstrapProperties;
import com.kole.platform.identity.infrastructure.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    JwtProperties.class,
    AdminBootstrapProperties.class
})
public class KoleBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            KoleBackendApplication.class,
            args
        );
    }
}