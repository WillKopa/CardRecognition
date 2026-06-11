package com.WillKopa.CardIdentifier.config;

import com.WillKopa.CardIdentifier.filter.UserSyncFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Card Identifier application.
 * <p>
 * Configures Spring Security with OAuth2 JWT authentication and custom filter chains.
 * Public endpoints are accessible without authentication, while other endpoints require JWT tokens.
 * </p>
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private UserSyncFilter userSyncFilter;

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * Disables CSRF protection, configures authorization rules for public and authenticated endpoints,
     * sets up OAuth2 JWT resource server, and adds a custom user synchronization filter.
     * </p>
     *
     * @param http the HttpSecurity configuration object
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/swagger-ui/**",  "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll() // Public endpoints
                        .anyRequest().authenticated()               // Everything else requires login
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .addFilterAfter(userSyncFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}