package com.WillKopa.CardIdentifier.filter;

import com.WillKopa.CardIdentifier.exception.UserNotFoundException;
import com.WillKopa.CardIdentifier.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for synchronizing user data from JWT tokens.
 * <p>
 * This filter runs after JWT authentication to ensure that authenticated users
 * exist in the database. If a user doesn't exist, it creates a new user record
 * using information from the JWT token.
 * </p>
 */
@Component
@AllArgsConstructor
public class UserSyncFilter extends OncePerRequestFilter {
    private UserService userService;

    /**
     * Filters each request to synchronize user data from JWT token.
     * <p>
     * Extracts the JWT token from the authentication context and ensures
     * the user exists in the database, creating a new user if necessary.
     * </p>
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
             Jwt jwt = jwtAuth.getToken();
             try {
                 userService.getUser(jwt);
             } catch (UserNotFoundException _) {
             }
        }

        filterChain.doFilter(request, response);

    }
}
