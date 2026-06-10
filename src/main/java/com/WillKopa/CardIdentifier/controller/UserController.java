package com.WillKopa.CardIdentifier.controller;

import com.WillKopa.CardIdentifier.converter.UserConverter;
import com.WillKopa.CardIdentifier.dto.request.CardCollectionRequest;
import com.WillKopa.CardIdentifier.dto.response.UserResponse;
import com.WillKopa.CardIdentifier.exception.CardNotFoundException;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user card collections.
 * <p>
 * Provides endpoints for adding and removing cards from a user's collection.
 * All operations are authenticated using JWT tokens.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
    private UserService userService;

    /**
     * Adds a card to the authenticated user's collection.
     *
     * @param addRequest the request containing card details to add
     * @param jwt the JWT token containing authentication information
     * @return ResponseEntity containing the updated user response
     * @throws CardNotFoundException if the specified card cannot be found
     */
    @PostMapping("/addCard")
    public ResponseEntity<UserResponse> addCard(@RequestBody CardCollectionRequest addRequest, @AuthenticationPrincipal Jwt jwt) throws CardNotFoundException {
        log.info("Adding card {} with value {} to collection", addRequest.getCardId(), addRequest.getMarketValue());
        User user = userService.addCard(addRequest, jwt.getClaimAsString("email"));
        UserResponse response = UserConverter.toResponse(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Removes a card from the authenticated user's collection.
     *
     * @param removeRequest the request containing card details to remove
     * @param jwt the JWT token containing authentication information
     * @return ResponseEntity containing the updated user response
     * @throws CardNotFoundException if the specified card cannot be found
     */
    @PostMapping("/removeCard")
    public ResponseEntity<UserResponse> removeCard(@RequestBody CardCollectionRequest removeRequest, @AuthenticationPrincipal Jwt jwt) throws CardNotFoundException {
        User user = userService.removeCard(removeRequest, jwt.getClaimAsString("email"));
        UserResponse response = UserConverter.toResponse(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the user information for the authenticated user.
     * <p>
     * If the user does not exist, creates a new user with the email and name
     * from the JWT token.
     * </p>
     *
     * @param jwt the JWT token containing authentication information
     * @return ResponseEntity containing the user response
     */
    @GetMapping("/getUserInfo")
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getOrCreateUser(jwt);
        UserResponse response = UserConverter.toResponse(user);
        return ResponseEntity.ok(response);
    }
}
