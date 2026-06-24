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

    private static final int TEST_USER_ID = 1;
    private static final int TEST_CARD_ID = 1;
    private static final int NON_EXISTENT_CARD_ID = 999;
    private static final int QUANTITY_ONE = 1;
    private static final int QUANTITY_TWO = 2;
    private static final float TEST_MARKET_PRICE_NORMAL = 10.5f;
    private static final float TEST_MARKET_PRICE_HOLO = 25.0f;
    private static final float TEST_MARKET_PRICE_REVERSE_HOLO = 15.75f;
    private static final String INITIAL_COLLECTION_VALUE = "100.00";
    private static final String COLLECTION_VALUE_AFTER_ADD = "110.50";
    private static final String COLLECTION_VALUE_AFTER_REMOVE = "89.50";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "testuser";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";
    private static final String TEST_CARD_NAME = "Pikachu";
    private static final String JWT_EMAIL_CLAIM = "email";
    private static final String JWT_NAME_CLAIM = "name";
    private static final int FIRST_CARD_INDEX = 0;

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
            .id(TEST_USER_ID)
            .email(TEST_EMAIL)
            .userName(TEST_USERNAME)
            .collectionValue(new BigDecimal(INITIAL_COLLECTION_VALUE))
            .cardList(new ArrayList<>())
            .build();

        card = new Card();
        card.setId(TEST_CARD_ID);
        card.setName(TEST_CARD_NAME);
        card.setMarketPriceNormal(TEST_MARKET_PRICE_NORMAL);
        card.setMarketPriceHolo(TEST_MARKET_PRICE_HOLO);
        card.setMarketPriceReverseHolo(TEST_MARKET_PRICE_REVERSE_HOLO);
    }

    @Test
    void testGetUser_Success() {
        when(jwt.getClaimAsString(JWT_EMAIL_CLAIM)).thenReturn(TEST_EMAIL);
        when(jwt.getClaimAsString(JWT_NAME_CLAIM)).thenReturn(TEST_USERNAME);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        User result = userService.getUser(jwt);

        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_USERNAME, result.getUserName());
        
        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void testGetUser_UserNotFound() {
        when(jwt.getClaimAsString(JWT_EMAIL_CLAIM)).thenReturn(NON_EXISTENT_EMAIL);
        when(userRepo.findByEmail(NON_EXISTENT_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(jwt));
        
        verify(userRepo).findByEmail(NON_EXISTENT_EMAIL);
    }

    @Test
    void testCreateUser_Success() throws UserAlreadyExistsException {
        when(jwt.getClaimAsString(JWT_EMAIL_CLAIM)).thenReturn(TEST_EMAIL);
        when(jwt.getClaimAsString(JWT_NAME_CLAIM)).thenReturn(TEST_USERNAME);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(jwt);

        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_USERNAME, result.getUserName());
        
        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(userRepo).save(any(User.class));
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        when(jwt.getClaimAsString(JWT_EMAIL_CLAIM)).thenReturn(TEST_EMAIL);
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(jwt));
        
        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void testAddCard_NewCard() throws CardNotFoundException {
        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(TEST_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_NORMAL)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(cardRepo.getReferenceById(TEST_CARD_ID)).thenReturn(card);

        User result = userService.addCard(request, TEST_EMAIL);

        assertNotNull(result);
        assertEquals(QUANTITY_ONE, result.getCardList().size());
        assertEquals(new BigDecimal(COLLECTION_VALUE_AFTER_ADD), result.getCollectionValue());
        assertEquals(QUANTITY_ONE, result.getCardList().get(FIRST_CARD_INDEX).getQuantity());
        
        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(cardRepo).getReferenceById(TEST_CARD_ID);
    }

    @Test
    void testAddCard_ExistingCard() throws CardNotFoundException {
        UserCard existingUserCard = UserCard.builder()
            .card(card)
            .quantity(QUANTITY_ONE)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
        user.getCardList().add(existingUserCard);

        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(TEST_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_NORMAL)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        User result = userService.addCard(request, TEST_EMAIL);

        assertNotNull(result);
        assertEquals(QUANTITY_ONE, result.getCardList().size());
        assertEquals(QUANTITY_TWO, result.getCardList().get(FIRST_CARD_INDEX).getQuantity());
        assertEquals(new BigDecimal(COLLECTION_VALUE_AFTER_ADD), result.getCollectionValue());
        
        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(cardRepo, never()).getReferenceById(anyInt());
    }

    @Test
    void testAddCard_CardNotFound() {
        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(NON_EXISTENT_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_NORMAL)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(cardRepo.getReferenceById(NON_EXISTENT_CARD_ID)).thenThrow(new EntityNotFoundException());

        assertThrows(CardNotFoundException.class, () -> userService.addCard(request, TEST_EMAIL));
        
        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(cardRepo).getReferenceById(NON_EXISTENT_CARD_ID);
    }

    @Test
    void testAddCard_OverloadedMethod() {
        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(TEST_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_NORMAL)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(cardRepo.getReferenceById(TEST_CARD_ID)).thenReturn(card);

        User result = userService.addCard(TEST_CARD_ID, TEST_MARKET_PRICE_NORMAL, CardCondition.NEAR_MINT, CardVariation.NORMAL, TEST_EMAIL);

        assertNotNull(result);
        assertEquals(QUANTITY_ONE, result.getCardList().size());
        
        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(cardRepo).getReferenceById(TEST_CARD_ID);
    }

    @Test
    void testRemoveCard_QuantityGreaterThanOne() {
        UserCard userCard = UserCard.builder()
            .card(card)
            .quantity(QUANTITY_TWO)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
        user.getCardList().add(userCard);

        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(TEST_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_NORMAL)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        User result = userService.removeCard(request, TEST_EMAIL);

        assertNotNull(result);
        assertEquals(QUANTITY_ONE, result.getCardList().size());
        assertEquals(QUANTITY_ONE, result.getCardList().get(FIRST_CARD_INDEX).getQuantity());
        assertEquals(new BigDecimal(COLLECTION_VALUE_AFTER_REMOVE), result.getCollectionValue());

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void testRemoveCard_QuantityEqualsOne() {
        UserCard userCard = UserCard.builder()
            .card(card)
            .quantity(QUANTITY_ONE)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
        user.getCardList().add(userCard);

        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(TEST_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_NORMAL)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        User result = userService.removeCard(request, TEST_EMAIL);

        assertNotNull(result);
        assertTrue(result.getCardList().isEmpty());
        assertEquals(new BigDecimal(COLLECTION_VALUE_AFTER_REMOVE), result.getCollectionValue());

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void testRemoveCard_DifferentConditionOrVariation() {
        UserCard userCard = UserCard.builder()
            .card(card)
            .quantity(QUANTITY_ONE)
            .cardCondition(CardCondition.LIGHTLY_PLAYED)
            .cardVariation(CardVariation.HOLOGRAPHIC)
            .build();
        user.getCardList().add(userCard);

        CardCollectionRequest request = CardCollectionRequest.builder()
            .cardId(TEST_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_NORMAL)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        User result = userService.removeCard(request, TEST_EMAIL);

        assertNotNull(result);
        assertEquals(QUANTITY_ONE, result.getCardList().size());
        assertEquals(new BigDecimal(INITIAL_COLLECTION_VALUE), result.getCollectionValue());

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepo).deleteByEmail(TEST_EMAIL);

        userService.deleteUser(TEST_EMAIL);

        verify(userRepo).deleteByEmail(TEST_EMAIL);
    }

    @Test
    void testAddCard_MultipleCardsWithDifferentConditions() throws CardNotFoundException {
        CardCollectionRequest request1 = CardCollectionRequest.builder()
            .cardId(TEST_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_NORMAL)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        CardCollectionRequest request2 = CardCollectionRequest.builder()
            .cardId(TEST_CARD_ID)
            .marketValue(TEST_MARKET_PRICE_HOLO)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.HOLOGRAPHIC)
            .build();


        BigDecimal COLLECTION_VALUE_MULTIPLE_CARDS = new BigDecimal(INITIAL_COLLECTION_VALUE).add(new BigDecimal(Float.toString(TEST_MARKET_PRICE_HOLO))).add(new BigDecimal(Float.toString(TEST_MARKET_PRICE_NORMAL)));

        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(cardRepo.getReferenceById(TEST_CARD_ID)).thenReturn(card);

        userService.addCard(request1, TEST_EMAIL);
        userService.addCard(request2, TEST_EMAIL);

        assertEquals(QUANTITY_TWO, user.getCardList().size());
        assertEquals(COLLECTION_VALUE_MULTIPLE_CARDS, user.getCollectionValue());

        verify(userRepo, times(QUANTITY_TWO)).findByEmail(TEST_EMAIL);
        verify(cardRepo, times(QUANTITY_TWO)).getReferenceById(TEST_CARD_ID);
    }
}
