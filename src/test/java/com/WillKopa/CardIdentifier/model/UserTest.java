package com.WillKopa.CardIdentifier.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getUserName());
        assertNotNull(user.getCollectionValue());
        assertEquals(BigDecimal.ZERO, user.getCollectionValue());
        assertNotNull(user.getCardList());
        assertTrue(user.getCardList().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        List<UserCard> cardList = new ArrayList<>();
        cardList.add(new UserCard());
        
        User user = new User(
            1,
            "test@example.com",
            "testuser",
            new BigDecimal("100.50"),
            cardList
        );

        assertEquals(1, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUserName());
        assertEquals(new BigDecimal("100.50"), user.getCollectionValue());
        assertEquals(1, user.getCardList().size());
    }

    @Test
    void testBuilder() {
        List<UserCard> cardList = new ArrayList<>();
        cardList.add(new UserCard());
        
        User user = User.builder()
            .id(1)
            .email("test@example.com")
            .userName("testuser")
            .collectionValue(new BigDecimal("100.50"))
            .cardList(cardList)
            .build();

        assertEquals(1, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUserName());
        assertEquals(new BigDecimal("100.50"), user.getCollectionValue());
        assertEquals(1, user.getCardList().size());
    }

    @Test
    void testBuilderWithDefaults() {
        User user = User.builder()
            .id(1)
            .email("test@example.com")
            .userName("testuser")
            .build();

        assertEquals(1, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUserName());
        assertEquals(BigDecimal.ZERO, user.getCollectionValue());
        assertNotNull(user.getCardList());
        assertTrue(user.getCardList().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setUserName("testuser");
        user.setCollectionValue(new BigDecimal("100.50"));
        
        List<UserCard> cardList = new ArrayList<>();
        cardList.add(new UserCard());
        user.setCardList(cardList);

        assertEquals(1, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUserName());
        assertEquals(new BigDecimal("100.50"), user.getCollectionValue());
        assertEquals(1, user.getCardList().size());
    }

    @Test
    void testAddCardToCollection() {
        User user = User.builder()
            .id(1)
            .email("test@example.com")
            .userName("testuser")
            .build();

        Card card = new Card();
        card.setId(1);
        card.setName("Pikachu");

        UserCard userCard = UserCard.builder()
            .card(card)
            .quantity(1)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        user.getCardList().add(userCard);

        assertEquals(1, user.getCardList().size());
        assertEquals("Pikachu", user.getCardList().get(0).getCard().getName());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
            .id(1)
            .email("test@example.com")
            .build();

        User user2 = User.builder()
            .id(1)
            .email("test@example.com")
            .build();

        User user3 = User.builder()
            .id(2)
            .email("other@example.com")
            .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
    }

    @Test
    void testToString() {
        User user = User.builder()
            .id(1)
            .email("test@example.com")
            .userName("testuser")
            .build();

        String toString = user.toString();
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("testuser"));
    }
}
