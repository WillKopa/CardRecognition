package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.dto.request.CardCollectionRequest;
import com.WillKopa.CardIdentifier.exception.CardNotFoundException;
import com.WillKopa.CardIdentifier.exception.UserAlreadyExistsException;
import com.WillKopa.CardIdentifier.exception.UserNotFoundException;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardCondition;
import com.WillKopa.CardIdentifier.model.CardVariation;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.model.UserCard;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import com.WillKopa.CardIdentifier.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private CardRepo cardRepo;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserService userService;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1)
            .email("test@example.com")
            .userName("testuser")
            .collectionValue(new BigDecimal("100.00"))
            .cardList(new ArrayList<>())
            .build();

        card = new Card();
        card.setId(1);
        card.setName("Pikachu");
        card.setMarketPriceNormal(10.5f);
        card.setMarketPriceHolo(25.0f);
        card.setMarketPriceReverseHolo(15.75f);
    }

    @Test
    void testGetUser_Success() {
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(jwt.getClaimAsString("name")).thenReturn("testuser");
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUser(jwt);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUserName());
        
        verify(userRepo).findByEmail("test@example.com");
    }

    @Test
    void testGetUser_UserNotFound() {
        when(jwt.getClaimAsString("email")).thenReturn("nonexistent@example.com");
        when(userRepo.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(jwt));
        
        verify(userRepo).findByEmail("nonexistent@example.com");
    }

    @Test
    void testCreateUser_Success() throws UserAlreadyExistsException {
        when(jwt.getClaimAsString("email")).thenReturn("new@example.com");
        when(jwt.getClaimAsString("name")).thenReturn("newuser");
        when(userRepo.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(jwt);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("newuser", result.getUserName());
        
        verify(userRepo).findByEmail("new@example.com");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(jwt));
        
        verify(userRepo).findByEmail("test@example.com");
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void testAddCard_NewCard() throws CardNotFoundException {
        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(1)
            .marketValue(10.5f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardRepo.getReferenceById(1)).thenReturn(card);

        User result = userService.addCard(request, "test@example.com");

        assertNotNull(result);
        assertEquals(1, result.getCardList().size());
        assertEquals(new BigDecimal("110.50"), result.getCollectionValue());
        assertEquals(1, result.getCardList().get(0).getQuantity());
        
        verify(userRepo).findByEmail("test@example.com");
        verify(cardRepo).getReferenceById(1);
    }

    @Test
    void testAddCard_ExistingCard() throws CardNotFoundException {
        UserCard existingUserCard = UserCard.builder()
            .card(card)
            .quantity(1)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
        user.getCardList().add(existingUserCard);

        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(1)
            .marketValue(10.5f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.addCard(request, "test@example.com");

        assertNotNull(result);
        assertEquals(1, result.getCardList().size());
        assertEquals(2, result.getCardList().get(0).getQuantity());
        assertEquals(new BigDecimal("110.50"), result.getCollectionValue());
        
        verify(userRepo).findByEmail("test@example.com");
        verify(cardRepo, never()).getReferenceById(anyInt());
    }

    @Test
    void testAddCard_CardNotFound() {
        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(999)
            .marketValue(10.5f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardRepo.getReferenceById(999)).thenThrow(new EntityNotFoundException());

        assertThrows(CardNotFoundException.class, () -> userService.addCard(request, "test@example.com"));
        
        verify(userRepo).findByEmail("test@example.com");
        verify(cardRepo).getReferenceById(999);
    }

    @Test
    void testAddCard_OverloadedMethod() {
        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(1)
            .marketValue(10.5f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardRepo.getReferenceById(1)).thenReturn(card);

        User result = userService.addCard(1, 10.5f, CardCondition.NEAR_MINT, CardVariation.NORMAL, "test@example.com");

        assertNotNull(result);
        assertEquals(1, result.getCardList().size());
        
        verify(userRepo).findByEmail("test@example.com");
        verify(cardRepo).getReferenceById(1);
    }

    @Test
    void testRemoveCard_QuantityGreaterThanOne() {
        UserCard userCard = UserCard.builder()
            .card(card)
            .quantity(2)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
        user.getCardList().add(userCard);

        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(1)
            .marketValue(10.5f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.removeCard(request, "test@example.com");

        assertNotNull(result);
        assertEquals(1, result.getCardList().size());
        assertEquals(1, result.getCardList().get(0).getQuantity());
        assertEquals(new BigDecimal("89.50"), result.getCollectionValue());
        
        verify(userRepo).findByEmail("test@example.com");
    }

    @Test
    void testRemoveCard_QuantityEqualsOne() {
        UserCard userCard = UserCard.builder()
            .card(card)
            .quantity(1)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
        user.getCardList().add(userCard);

        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(1)
            .marketValue(10.5f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.removeCard(request, "test@example.com");

        assertNotNull(result);
        assertTrue(result.getCardList().isEmpty());
        assertEquals(new BigDecimal("89.50"), result.getCollectionValue());
        
        verify(userRepo).findByEmail("test@example.com");
    }

    @Test
    void testRemoveCard_DifferentConditionOrVariation() {
        UserCard userCard = UserCard.builder()
            .card(card)
            .quantity(1)
            .cardCondition(CardCondition.LIGHTLY_PLAYED)
            .cardVariation(CardVariation.HOLOGRAPHIC)
            .build();
        user.getCardList().add(userCard);

        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(1)
            .marketValue(10.5f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.removeCard(request, "test@example.com");

        assertNotNull(result);
        assertEquals(1, result.getCardList().size());
        assertEquals(new BigDecimal("100.00"), result.getCollectionValue());
        
        verify(userRepo).findByEmail("test@example.com");
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepo).deleteByEmail("test@example.com");

        userService.deleteUser("test@example.com");

        verify(userRepo).deleteByEmail("test@example.com");
    }

    @Test
    void testAddCard_MultipleCardsWithDifferentConditions() throws CardNotFoundException {
        CardCollectionRequest request1 = CardCollectionRequest.builder()
            .cardId(1)
            .marketValue(10.5f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        CardCollectionRequest request2 = CardCollectionRequest.builder()
            .cardId(1)
            .marketValue(25.0f)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.HOLOGRAPHIC)
            .build();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardRepo.getReferenceById(1)).thenReturn(card);

        userService.addCard(request1, "test@example.com");
        userService.addCard(request2, "test@example.com");

        assertEquals(2, user.getCardList().size());
        assertEquals(new BigDecimal("136.00"), user.getCollectionValue());
        
        verify(userRepo, times(2)).findByEmail("test@example.com");
        verify(cardRepo, times(2)).getReferenceById(1);
    }
}
