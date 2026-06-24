package com.WillKopa.CardIdentifier.controller;

import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.model.CardCondition;
import com.WillKopa.CardIdentifier.model.CardVariation;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.service.CardService;
import com.WillKopa.CardIdentifier.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {
    private static final Integer ID = 1;
    private static final String EMAIL = "test@example.com";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpassword";
    private static final String CARD_NAME = "Pikachu";
    private static final String CARD_NUMBER = "001";
    private static final Integer SET_PRINTED_TOTAL = 102;
    private static final String EXTERNAL_DB_ID = "base1-1";
    private static final String CARD_SET = "Base Set";
    private static final Float MARKET_PRICE_NORMAL = 10.5f;
    private static final Float MARKET_PRICE_HOLO = 25.0f;
    private static final Float MARKET_PRICE_REVERSE_HOLO = 15.75f;
    private static final String CARD_CONDITION = "Near Mint";
    private static final String CARD_VARIATION = "Base Set";
    private static final String CARD_IMAGE_LOW = "low.jpg";
    private static final String CARD_IMAGE_HIGH = "high.jpg";

    @Mock
    private CardService cardService;

    @Mock
    private UserService userService;

    @Mock
    private MultipartFile imageFile;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private CardController cardController;

    private CardSearchResult cardSearchResult;
    private User user;

    @BeforeEach
    void setUp() {
        cardSearchResult = new CardSearchResult(
            ID,
            CARD_NAME,
            CARD_NUMBER,
            SET_PRINTED_TOTAL,
            EXTERNAL_DB_ID,
            CARD_SET,
            MARKET_PRICE_NORMAL,
            MARKET_PRICE_HOLO,
            MARKET_PRICE_REVERSE_HOLO,
            CARD_IMAGE_LOW,
            CARD_IMAGE_HIGH
        );

        user = User.builder()
            .id(ID)
            .email(EMAIL)
            .userName(USERNAME)
            .build();
    }

    @Test
    void testIdentifyCard_Success() throws NoOcrResultException, InvalidImageException {
        List<CardSearchResult> results = List.of(cardSearchResult);
        when(cardService.identifyCard(imageFile)).thenReturn(results);

        ResponseEntity<List<CardSearchResult>> response = cardController.identifyCard(imageFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ID, response.getBody().size());
        assertEquals(CARD_NAME, response.getBody().getFirst().getName());

        verify(cardService).identifyCard(imageFile);
    }

    @Test
    void testIdentifyCard_EmptyResults() throws NoOcrResultException, InvalidImageException {
        when(cardService.identifyCard(imageFile)).thenReturn(new ArrayList<>());

        ResponseEntity<List<CardSearchResult>> response = cardController.identifyCard(imageFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(cardService).identifyCard(imageFile);
    }

    @Test
    void testIdentifyCard_ThrowsNoOcrResultException() throws NoOcrResultException, InvalidImageException {
        when(cardService.identifyCard(imageFile)).thenThrow(new NoOcrResultException("No OCR result"));

        assertThrows(NoOcrResultException.class, () -> cardController.identifyCard(imageFile));

        verify(cardService).identifyCard(imageFile);
    }

    @Test
    void testIdentifyCard_ThrowsInvalidImageException() throws NoOcrResultException, InvalidImageException {
        when(cardService.identifyCard(imageFile)).thenThrow(new InvalidImageException("Invalid image"));

        assertThrows(InvalidImageException.class, () -> cardController.identifyCard(imageFile));

        verify(cardService).identifyCard(imageFile);
    }

    @Test
    void testIdentifyAndAddToCollection_Success() throws NoOcrResultException, InvalidImageException {
        List<CardSearchResult> results = List.of(cardSearchResult);
        when(cardService.identifyCard(imageFile)).thenReturn(results);
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.addCard(eq(ID), eq(MARKET_PRICE_NORMAL), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq(EMAIL)))
            .thenReturn(user);

        ResponseEntity<User> response = cardController.identifyAndAddToCollection(
            imageFile,
            CardCondition.NEAR_MINT,
            CardVariation.NORMAL,
            jwt
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(EMAIL, response.getBody().getEmail());

        verify(cardService).identifyCard(imageFile);
        verify(userService).addCard(eq(ID), eq(MARKET_PRICE_NORMAL), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq(EMAIL));
    }

    @Test
    void testIdentifyAndAddToCollection_Holographic() throws NoOcrResultException, InvalidImageException {
        List<CardSearchResult> results = List.of(cardSearchResult);
        when(cardService.identifyCard(imageFile)).thenReturn(results);
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.addCard(eq(ID), eq(MARKET_PRICE_HOLO), eq(CardCondition.NEAR_MINT), eq(CardVariation.HOLOGRAPHIC), eq(EMAIL)))
            .thenReturn(user);

        ResponseEntity<User> response = cardController.identifyAndAddToCollection(
            imageFile,
            CardCondition.NEAR_MINT,
            CardVariation.HOLOGRAPHIC,
            jwt
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(cardService).identifyCard(imageFile);
        verify(userService).addCard(eq(ID), eq(MARKET_PRICE_HOLO), eq(CardCondition.NEAR_MINT), eq(CardVariation.HOLOGRAPHIC), eq(EMAIL));
    }

    @Test
    void testIdentifyAndAddToCollection_ReverseHolographic() throws NoOcrResultException, InvalidImageException {
        List<CardSearchResult> results = List.of(cardSearchResult);
        when(cardService.identifyCard(imageFile)).thenReturn(results);
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.addCard(eq(ID), eq(MARKET_PRICE_REVERSE_HOLO), eq(CardCondition.NEAR_MINT), eq(CardVariation.REVERSE_HOLOGRAPHIC), eq(EMAIL)))
            .thenReturn(user);

        ResponseEntity<User> response = cardController.identifyAndAddToCollection(
            imageFile,
            CardCondition.NEAR_MINT,
            CardVariation.REVERSE_HOLOGRAPHIC,
            jwt
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(cardService).identifyCard(imageFile);
        verify(userService).addCard(eq(ID), eq(MARKET_PRICE_REVERSE_HOLO), eq(CardCondition.NEAR_MINT), eq(CardVariation.REVERSE_HOLOGRAPHIC), eq(EMAIL));
    }

    @Test
    void testIdentifyAndAddToCollection_MultipleResults() throws NoOcrResultException, InvalidImageException {
        CardSearchResult result2 = new CardSearchResult(
            2,
            "Charizard",
            "004",
            102,
            "base1-4",
            "Base Set",
            100.0f,
            250.0f,
            150.0f,
            "low2.jpg",
            "high2.jpg"
        );
        List<CardSearchResult> results = List.of(cardSearchResult, result2);

        when(cardService.identifyCard(imageFile)).thenReturn(results);
        when(jwt.getClaimAsString("email")).thenReturn(EMAIL);
        when(userService.addCard(eq(ID), eq(MARKET_PRICE_NORMAL), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq(EMAIL)))
            .thenReturn(user);
        when(userService.addCard(eq(2), eq(100.0f), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq(EMAIL)))
            .thenReturn(user);

        ResponseEntity<User> response = cardController.identifyAndAddToCollection(
            imageFile,
            CardCondition.NEAR_MINT,
            CardVariation.NORMAL,
            jwt
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(cardService).identifyCard(imageFile);
        verify(userService, times(2)).addCard(anyInt(), anyFloat(), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq(EMAIL));
    }

    @Test
    void testIdentifyAndAddToCollection_ThrowsNoOcrResultException() throws NoOcrResultException, InvalidImageException {
        when(cardService.identifyCard(imageFile)).thenThrow(new NoOcrResultException("No OCR result"));

        assertThrows(NoOcrResultException.class, () ->
            cardController.identifyAndAddToCollection(imageFile, CardCondition.NEAR_MINT, CardVariation.NORMAL, jwt));

        verify(cardService).identifyCard(imageFile);
        verify(userService, never()).addCard(anyInt(), anyFloat(), any(), any(), anyString());
    }

    @Test
    void testHelloWorld() {
        ResponseEntity<String> response = cardController.helloWorld();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello World Private", response.getBody());
    }

    @Test
    void testHelloWorldPublic() {
        ResponseEntity<String> response = cardController.helloWorldPublic();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello World Public", response.getBody());
    }
}
