package com.WillKopa.CardIdentifier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error").permitAll() // Public endpoints
                        .anyRequest().authenticated()               // Everything else requires login
                )
                .oauth2Login(oauth2 -> oauth2
                        // This enables the standard Google/OIDC login flow
                        .defaultSuccessUrl("/loginSuccess", true)
                );

        return http.build();
    }
}