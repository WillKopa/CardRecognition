package com.WillKopa.CardIdentifier.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserCardTest {

    @Test
    void testNoArgsConstructor() {
        UserCard userCard = new UserCard();
        assertNotNull(userCard);
        assertNull(userCard.getId());
        assertNull(userCard.getUser());
        assertNull(userCard.getCard());
        assertNull(userCard.getQuantity());
        assertNull(userCard.getCardCondition());
        assertNull(userCard.getCardVariation());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setId(1);
        
        Card card = new Card();
        card.setId(1);
        
        UserCard userCard = new UserCard(
            1,
            user,
            card,
            2,
            CardCondition.NEAR_MINT,
            CardVariation.HOLOGRAPHIC
        );

        assertEquals(1, userCard.getId());
        assertEquals(user, userCard.getUser());
        assertEquals(card, userCard.getCard());
        assertEquals(2, userCard.getQuantity());
        assertEquals(CardCondition.NEAR_MINT, userCard.getCardCondition());
        assertEquals(CardVariation.HOLOGRAPHIC, userCard.getCardVariation());
    }

    @Test
    void testBuilder() {
        User user = new User();
        user.setId(1);
        
        Card card = new Card();
        card.setId(1);
        
        UserCard userCard = UserCard.builder()
            .id(1)
            .user(user)
            .card(card)
            .quantity(3)
            .cardCondition(CardCondition.LIGHTLY_PLAYED)
            .cardVariation(CardVariation.REVERSE_HOLOGRAPHIC)
            .build();

        assertEquals(1, userCard.getId());
        assertEquals(user, userCard.getUser());
        assertEquals(card, userCard.getCard());
        assertEquals(3, userCard.getQuantity());
        assertEquals(CardCondition.LIGHTLY_PLAYED, userCard.getCardCondition());
        assertEquals(CardVariation.REVERSE_HOLOGRAPHIC, userCard.getCardVariation());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setId(1);
        
        Card card = new Card();
        card.setId(1);
        
        UserCard userCard = new UserCard();
        userCard.setId(1);
        userCard.setUser(user);
        userCard.setCard(card);
        userCard.setQuantity(5);
        userCard.setCardCondition(CardCondition.MODERATELY_PLAYED);
        userCard.setCardVariation(CardVariation.NORMAL);

        assertEquals(1, userCard.getId());
        assertEquals(user, userCard.getUser());
        assertEquals(card, userCard.getCard());
        assertEquals(5, userCard.getQuantity());
        assertEquals(CardCondition.MODERATELY_PLAYED, userCard.getCardCondition());
        assertEquals(CardVariation.NORMAL, userCard.getCardVariation());
    }

    @Test
    void testEqualsAndHashCode() {
        User user = new User();
        user.setId(1);
        
        Card card = new Card();
        card.setId(1);
        
        UserCard userCard1 = UserCard.builder()
            .id(1)
            .user(user)
            .card(card)
            .quantity(1)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        UserCard userCard2 = UserCard.builder()
            .id(1)
            .user(user)
            .card(card)
            .quantity(1)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        UserCard userCard3 = UserCard.builder()
            .id(2)
            .user(user)
            .card(card)
            .quantity(1)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        assertEquals(userCard1, userCard2);
        assertEquals(userCard1.hashCode(), userCard2.hashCode());
        assertNotEquals(userCard1, userCard3);
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1);
        
        Card card = new Card();
        card.setId(1);
        
        UserCard userCard = UserCard.builder()
            .id(1)
            .user(user)
            .card(card)
            .quantity(1)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        String toString = userCard.toString();
        assertTrue(toString.contains("1"));
    }

    @Test
    void testCardConditionEnum() {
        assertEquals(5, CardCondition.values().length);
        assertEquals(CardCondition.NEAR_MINT, CardCondition.valueOf("NEAR_MINT"));
        assertEquals(CardCondition.LIGHTLY_PLAYED, CardCondition.valueOf("LIGHTLY_PLAYED"));
        assertEquals(CardCondition.MODERATELY_PLAYED, CardCondition.valueOf("MODERATELY_PLAYED"));
        assertEquals(CardCondition.HEAVILY_PLAYED, CardCondition.valueOf("HEAVILY_PLAYED"));
        assertEquals(CardCondition.DAMAGED, CardCondition.valueOf("DAMAGED"));
    }

    @Test
    void testCardVariationEnum() {
        assertEquals(4, CardVariation.values().length);
        assertEquals(CardVariation.NORMAL, CardVariation.valueOf("NORMAL"));
        assertEquals(CardVariation.HOLOGRAPHIC, CardVariation.valueOf("HOLOGRAPHIC"));
        assertEquals(CardVariation.REVERSE_HOLOGRAPHIC, CardVariation.valueOf("REVERSE_HOLOGRAPHIC"));
        assertEquals(CardVariation.SPECIAL, CardVariation.valueOf("SPECIAL"));
    }
}
