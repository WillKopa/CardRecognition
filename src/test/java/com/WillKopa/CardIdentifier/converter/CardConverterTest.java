package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import com.WillKopa.CardIdentifier.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardConverterTest {

    private CardConverter cardConverter;
    private Card card;

    @BeforeEach
    void setUp() {
        cardConverter = new CardConverter();
        card = new Card();
        card.setId(1);
        card.setName("Pikachu");
        card.setCardNumber("001");
        card.setSetOfficialPrintedTotal(102);
        card.setExternalDbId("base1-1");
        card.setCardSet("Base Set");
        card.setMarketPriceNormal(10.5f);
        card.setMarketPriceHolo(25.0f);
        card.setMarketPriceReverseHolo(15.75f);
        card.setImageUrlLow("low.jpg");
        card.setImageUrlHigh("high.jpg");
    }

    @Test
    void testToCardSearchResult_Success() {
        CardSearchResult result = cardConverter.toCardSearchResult(card);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Pikachu", result.getName());
        assertEquals("001", result.getCardNumber());
        assertEquals(102, result.getSetPrintedTotal());
        assertEquals("base1-1", result.getExternalDbId());
        assertEquals("Base Set", result.getCardSet());
        assertEquals(10.5f, result.getMarketPriceNormal());
        assertEquals(25.0f, result.getMarketPriceHolo());
        assertEquals(15.75f, result.getMarketPriceReverseHolo());
        assertEquals("low.jpg", result.getImageUrlLow());
        assertEquals("high.jpg", result.getImageUrlHigh());
    }

    @Test
    void testToCardSearchResult_NullInput() {
        CardSearchResult result = cardConverter.toCardSearchResult(null);

        assertNull(result);
    }

    @Test
    void testToCardSearchResult_NullFields() {
        Card nullCard = new Card();
        nullCard.setId(1);
        nullCard.setName("Pikachu");

        CardSearchResult result = cardConverter.toCardSearchResult(nullCard);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Pikachu", result.getName());
        assertNull(result.getCardNumber());
        assertNull(result.getExternalDbId());
        assertNull(result.getCardSet());
        assertNull(result.getMarketPriceNormal());
        assertNull(result.getMarketPriceHolo());
        assertNull(result.getMarketPriceReverseHolo());
        assertNull(result.getImageUrlLow());
        assertNull(result.getImageUrlHigh());
    }

    @Test
    void testToCardSearchResult_ZeroPrices() {
        card.setMarketPriceNormal(0f);
        card.setMarketPriceHolo(0f);
        card.setMarketPriceReverseHolo(0f);

        CardSearchResult result = cardConverter.toCardSearchResult(card);

        assertNotNull(result);
        assertEquals(0f, result.getMarketPriceNormal());
        assertEquals(0f, result.getMarketPriceHolo());
        assertEquals(0f, result.getMarketPriceReverseHolo());
    }

    @Test
    void testToCardSearchResult_LargePrices() {
        card.setMarketPriceNormal(1000.0f);
        card.setMarketPriceHolo(2500.0f);
        card.setMarketPriceReverseHolo(1500.0f);

        CardSearchResult result = cardConverter.toCardSearchResult(card);

        assertNotNull(result);
        assertEquals(1000.0f, result.getMarketPriceNormal());
        assertEquals(2500.0f, result.getMarketPriceHolo());
        assertEquals(1500.0f, result.getMarketPriceReverseHolo());
    }

    @Test
    void testToCardSearchResult_EmptyStrings() {
        card.setCardNumber("");
        card.setExternalDbId("");
        card.setCardSet("");
        card.setImageUrlLow("");
        card.setImageUrlHigh("");

        CardSearchResult result = cardConverter.toCardSearchResult(card);

        assertNotNull(result);
        assertEquals("", result.getCardNumber());
        assertEquals("", result.getExternalDbId());
        assertEquals("", result.getCardSet());
        assertEquals("", result.getImageUrlLow());
        assertEquals("", result.getImageUrlHigh());
    }
}
