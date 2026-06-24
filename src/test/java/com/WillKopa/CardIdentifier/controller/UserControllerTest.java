package com.WillKopa.CardIdentifier.controller;

import com.WillKopa.CardIdentifier.converter.UserConverter;
import com.WillKopa.CardIdentifier.dto.request.CardCollectionRequest;
import com.WillKopa.CardIdentifier.dto.response.UserResponse;
import com.WillKopa.CardIdentifier.exception.CardNotFoundException;
import com.WillKopa.CardIdentifier.exception.UserAlreadyExistsException;
import com.WillKopa.CardIdentifier.exception.UserNotFoundException;
import com.WillKopa.CardIdentifier.model.CardCondition;
import com.WillKopa.CardIdentifier.model.CardVariation;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private static final Integer ID = 1;
    private static final String EMAIL = "test@example.com";
    private static final String USERNAME = "testuser";
    private static final BigDecimal COLLECTION_VALUE = new BigDecimal("100.00");
    private static final Float MARKET_VALUE = 10.5f;

    @Mock
    private UserService userService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserController userController;

    private User user;
    private CardCollectionRequest cardCollectionRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(ID)
            .email(EMAIL)
            .userName(USERNAME)
            .collectionValue(COLLECTION_VALUE)
            .cardList(new ArrayList<>())
            .build();

        cardCollectionRequest = CardCollectionRequest.builder()
            .cardId(ID)
            .marketValue(MARKET_VALUE)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
    }

    @Test
    void testAddCard_Success() {
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.addCard(cardCollectionRequest, EMAIL)).thenReturn(user);

        try (MockedStatic<UserConverter> mockedConverter = mockStatic(UserConverter.class)) {
            UserResponse userResponse = new UserResponse(USERNAME, COLLECTION_VALUE, new ArrayList<>());
            mockedConverter.when(() -> UserConverter.toResponse(user)).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.addCard(cardCollectionRequest, jwt);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(USERNAME, response.getBody().userName());
            assertEquals(COLLECTION_VALUE, response.getBody().collectionValue());

            verify(userService).addCard(cardCollectionRequest, EMAIL);
            mockedConverter.verify(() -> UserConverter.toResponse(user));
        }
    }

    @Test
    void testAddCard_ThrowsCardNotFoundException() {
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.addCard(cardCollectionRequest, EMAIL))
            .thenThrow(new CardNotFoundException("Card not found"));

        assertThrows(CardNotFoundException.class, () -> userController.addCard(cardCollectionRequest, jwt));

        verify(userService).addCard(cardCollectionRequest, EMAIL);
    }

    @Test
    void testRemoveCard_Success() {
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.removeCard(cardCollectionRequest, EMAIL)).thenReturn(user);

        try (MockedStatic<UserConverter> mockedConverter = mockStatic(UserConverter.class)) {
            UserResponse userResponse = new UserResponse(USERNAME, COLLECTION_VALUE, new ArrayList<>());
            mockedConverter.when(() -> UserConverter.toResponse(user)).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.removeCard(cardCollectionRequest, jwt);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(USERNAME, response.getBody().userName());
            assertEquals(COLLECTION_VALUE, response.getBody().collectionValue());

            verify(userService).removeCard(cardCollectionRequest, EMAIL);
            mockedConverter.verify(() -> UserConverter.toResponse(user));
        }
    }

    @Test
    void testRemoveCard_ThrowsCardNotFoundException() {
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.removeCard(cardCollectionRequest, EMAIL))
            .thenThrow(new CardNotFoundException("Card not found"));

        assertThrows(CardNotFoundException.class, () -> userController.removeCard(cardCollectionRequest, jwt));

        verify(userService).removeCard(cardCollectionRequest, EMAIL);
    }

    @Test
    void testGetUserInfo_Success() {
        when(userService.getUser(jwt)).thenReturn(user);

        try (MockedStatic<UserConverter> mockedConverter = mockStatic(UserConverter.class)) {
            UserResponse userResponse = new UserResponse(USERNAME, COLLECTION_VALUE, new ArrayList<>());
            mockedConverter.when(() -> UserConverter.toResponse(user)).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.getUserInfo(jwt);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(USERNAME, response.getBody().userName());

            verify(userService).getUser(jwt);
            mockedConverter.verify(() -> UserConverter.toResponse(user));
        }
    }

    @Test
    void testGetUserInfo_ThrowsUserNotFoundException() {
        when(userService.getUser(jwt)).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> userController.getUserInfo(jwt));

        verify(userService).getUser(jwt);
    }

    @Test
    void testCreateUser_Success() {
        when(userService.createUser(jwt)).thenReturn(user);

        try (MockedStatic<UserConverter> mockedConverter = mockStatic(UserConverter.class)) {
            UserResponse userResponse = new UserResponse(USERNAME, BigDecimal.ZERO, new ArrayList<>());
            mockedConverter.when(() -> UserConverter.toResponse(user)).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.createUser(jwt);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(USERNAME, response.getBody().userName());

            verify(userService).createUser(jwt);
            mockedConverter.verify(() -> UserConverter.toResponse(user));
        }
    }

    @Test
    void testCreateUser_ThrowsUserAlreadyExistsException() {
        when(userService.createUser(jwt)).thenThrow(new UserAlreadyExistsException());

        assertThrows(UserAlreadyExistsException.class, () -> userController.createUser(jwt));

        verify(userService).createUser(jwt);
    }

    @Test
    void testDeleteUser_Success() {
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        doNothing().when(userService).deleteUser(EMAIL);

        ResponseEntity<String> response = userController.deleteUser(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());

        verify(userService).deleteUser(EMAIL);
    }

    @Test
    void testAddCard_WithDifferentConditions() {
        CardCollectionRequest holoRequest = CardCollectionRequest.builder()
            .cardId(ID)
            .marketValue(MARKET_VALUE)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.HOLOGRAPHIC)
            .build();

        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.addCard(holoRequest, EMAIL)).thenReturn(user);

        try (MockedStatic<UserConverter> mockedConverter = mockStatic(UserConverter.class)) {
            UserResponse userResponse = new UserResponse(USERNAME, COLLECTION_VALUE, new ArrayList<>());
            mockedConverter.when(() -> UserConverter.toResponse(user)).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.addCard(holoRequest, jwt);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(USERNAME, response.getBody().userName());
            assertEquals(COLLECTION_VALUE, response.getBody().collectionValue());

            verify(userService).addCard(holoRequest, EMAIL);
            mockedConverter.verify(() -> UserConverter.toResponse(user));
        }
    }

    @Test
    void testRemoveCard_WithDifferentConditions() throws CardNotFoundException {
        CardCollectionRequest reverseHoloRequest = CardCollectionRequest.builder()
            .cardId(ID)
            .marketValue(MARKET_VALUE)
            .cardCondition(CardCondition.LIGHTLY_PLAYED)
            .cardVariation(CardVariation.REVERSE_HOLOGRAPHIC)
            .build();

        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.removeCard(reverseHoloRequest, EMAIL)).thenReturn(user);

        try (MockedStatic<UserConverter> mockedConverter = mockStatic(UserConverter.class)) {
            UserResponse userResponse = new UserResponse(USERNAME, COLLECTION_VALUE, new ArrayList<>());
            mockedConverter.when(() -> UserConverter.toResponse(user)).thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.removeCard(reverseHoloRequest, jwt);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            verify(userService).removeCard(reverseHoloRequest, EMAIL);
            mockedConverter.verify(() -> UserConverter.toResponse(user));
        }
    }
}
