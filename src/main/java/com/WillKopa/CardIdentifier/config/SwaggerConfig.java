package com.WillKopa.CardIdentifier.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for the Card Identifier application.
 * <p>
 * Configures the OpenAPI documentation with JWT Bearer authentication scheme.
 * This enables API documentation with authentication support for testing endpoints.
 * </p>
 */
@Configuration
public class SwaggerConfig {
    /**
     * Configures the OpenAPI specification for the application.
     * <p>
     * Sets up JWT Bearer authentication as the default security scheme for all endpoints.
     * </p>
     *
     * @return the configured OpenAPI specification
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("BearerAuthentication"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuthentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}