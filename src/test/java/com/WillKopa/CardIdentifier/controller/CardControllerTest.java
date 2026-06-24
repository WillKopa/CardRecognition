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
            1,
            "Pikachu",
            "001",
            102,
            "base1-1",
            "Base Set",
            10.5f,
            25.0f,
            15.75f,
            "low.jpg",
            "high.jpg"
        );

        user = User.builder()
            .id(1)
            .email("test@example.com")
            .userName("testuser")
            .build();
    }

    @Test
    void testIdentifyCard_Success() throws NoOcrResultException, InvalidImageException {
        List<CardSearchResult> results = List.of(cardSearchResult);
        when(cardService.identifyCard(imageFile)).thenReturn(results);

        ResponseEntity<List<CardSearchResult>> response = cardController.identifyCard(imageFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Pikachu", response.getBody().get(0).getName());

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
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(userService.addCard(eq(1), eq(10.5f), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq("test@example.com")))
            .thenReturn(user);

        ResponseEntity<User> response = cardController.identifyAndAddToCollection(
            imageFile,
            CardCondition.NEAR_MINT,
            CardVariation.NORMAL,
            jwt
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());

        verify(cardService).identifyCard(imageFile);
        verify(userService).addCard(eq(1), eq(10.5f), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq("test@example.com"));
    }

    @Test
    void testIdentifyAndAddToCollection_Holographic() throws NoOcrResultException, InvalidImageException {
        List<CardSearchResult> results = List.of(cardSearchResult);
        when(cardService.identifyCard(imageFile)).thenReturn(results);
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(userService.addCard(eq(1), eq(25.0f), eq(CardCondition.NEAR_MINT), eq(CardVariation.HOLOGRAPHIC), eq("test@example.com")))
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
        verify(userService).addCard(eq(1), eq(25.0f), eq(CardCondition.NEAR_MINT), eq(CardVariation.HOLOGRAPHIC), eq("test@example.com"));
    }

    @Test
    void testIdentifyAndAddToCollection_ReverseHolographic() throws NoOcrResultException, InvalidImageException {
        List<CardSearchResult> results = List.of(cardSearchResult);
        when(cardService.identifyCard(imageFile)).thenReturn(results);
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(userService.addCard(eq(1), eq(15.75f), eq(CardCondition.NEAR_MINT), eq(CardVariation.REVERSE_HOLOGRAPHIC), eq("test@example.com")))
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
        verify(userService).addCard(eq(1), eq(15.75f), eq(CardCondition.NEAR_MINT), eq(CardVariation.REVERSE_HOLOGRAPHIC), eq("test@example.com"));
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
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(userService.addCard(eq(1), eq(10.5f), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq("test@example.com")))
            .thenReturn(user);
        when(userService.addCard(eq(2), eq(100.0f), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq("test@example.com")))
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
        verify(userService, times(2)).addCard(anyInt(), anyFloat(), eq(CardCondition.NEAR_MINT), eq(CardVariation.NORMAL), eq("test@example.com"));
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
