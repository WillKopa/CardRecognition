package com.WillKopa.CardIdentifier.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testNoArgsConstructor() {
        Card card = new Card();
        assertNotNull(card);
        assertNull(card.getId());
        assertNull(card.getExternalDbId());
        assertNull(card.getGame());
        assertNull(card.getName());
        assertNull(card.getCardSet());
        assertNull(card.getCardSetId());
        assertNull(card.getCardNumber());
        assertEquals(0, card.getSetOfficialPrintedTotal());
        assertNull(card.getImageUrlLow());
        assertNull(card.getImageUrlHigh());
        assertNull(card.getMarketPriceNormal());
        assertNull(card.getMarketPriceHolo());
        assertNull(card.getMarketPriceReverseHolo());
    }

    @Test
    void testAllArgsConstructor() {
        Card card = new Card(
            1,
            "ext123",
            "Pokemon",
            "Pikachu",
            "Base Set",
            "base1",
            "001",
            102,
            "low.jpg",
            "high.jpg",
            10.5f,
            25.0f,
            15.75f
        );

        assertEquals(1, card.getId());
        assertEquals("ext123", card.getExternalDbId());
        assertEquals("Pokemon", card.getGame());
        assertEquals("Pikachu", card.getName());
        assertEquals("Base Set", card.getCardSet());
        assertEquals("base1", card.getCardSetId());
        assertEquals("001", card.getCardNumber());
        assertEquals(102, card.getSetOfficialPrintedTotal());
        assertEquals("low.jpg", card.getImageUrlLow());
        assertEquals("high.jpg", card.getImageUrlHigh());
        assertEquals(10.5f, card.getMarketPriceNormal());
        assertEquals(25.0f, card.getMarketPriceHolo());
        assertEquals(15.75f, card.getMarketPriceReverseHolo());
    }

    @Test
    void testSettersAndGetters() {
        Card card = new Card();
        card.setId(1);
        card.setExternalDbId("ext123");
        card.setGame("Pokemon");
        card.setName("Charizard");
        card.setCardSet("Base Set");
        card.setCardSetId("base1");
        card.setCardNumber("004");
        card.setSetOfficialPrintedTotal(102);
        card.setImageUrlLow("low.jpg");
        card.setImageUrlHigh("high.jpg");
        card.setMarketPriceNormal(100.0f);
        card.setMarketPriceHolo(250.0f);
        card.setMarketPriceReverseHolo(150.0f);

        assertEquals(1, card.getId());
        assertEquals("ext123", card.getExternalDbId());
        assertEquals("Pokemon", card.getGame());
        assertEquals("Charizard", card.getName());
        assertEquals("Base Set", card.getCardSet());
        assertEquals("base1", card.getCardSetId());
        assertEquals("004", card.getCardNumber());
        assertEquals(102, card.getSetOfficialPrintedTotal());
        assertEquals("low.jpg", card.getImageUrlLow());
        assertEquals("high.jpg", card.getImageUrlHigh());
        assertEquals(100.0f, card.getMarketPriceNormal());
        assertEquals(250.0f, card.getMarketPriceHolo());
        assertEquals(150.0f, card.getMarketPriceReverseHolo());
    }

    @Test
    void testEqualsAndHashCode() {
        Card card1 = new Card();
        card1.setId(1);
        card1.setName("Pikachu");

        Card card2 = new Card();
        card2.setId(1);
        card2.setName("Pikachu");

        Card card3 = new Card();
        card3.setId(2);
        card3.setName("Charizard");

        assertEquals(card1, card2);
        assertEquals(card1.hashCode(), card2.hashCode());
        assertNotEquals(card1, card3);
        assertNotEquals(card1.hashCode(), card3.hashCode());
    }

    @Test
    void testToString() {
        Card card = new Card();
        card.setId(1);
        card.setName("Pikachu");
        
        String toString = card.toString();
        assertTrue(toString.contains("Pikachu"));
        assertTrue(toString.contains("1"));
    }
}
