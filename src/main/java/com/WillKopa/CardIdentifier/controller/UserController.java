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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
    private UserService userService;

    @PostMapping("/addCard")
    public ResponseEntity<UserResponse> addCard(@RequestBody CardCollectionRequest addRequest, @AuthenticationPrincipal Jwt jwt) throws CardNotFoundException {
        User user = userService.addCard(addRequest, jwt.getClaimAsString("email"));
        UserResponse response = UserConverter.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/removeCard")
    public ResponseEntity<UserResponse> removeCard(@RequestBody CardCollectionRequest removeRequest, @AuthenticationPrincipal Jwt jwt) throws CardNotFoundException {
        User user = userService.removeCard(removeRequest, jwt.getClaimAsString("email"));
        UserResponse response = UserConverter.toResponse(user);
        return ResponseEntity.ok(response);
    }
}
