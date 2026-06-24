package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import com.WillKopa.CardIdentifier.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardConverterTest {
    private static final Integer ID = 1;
    private static final String CARD_NAME = "Pikachu";
    private static final String CARD_NUMBER = "001";
    private static final Integer SET_PRINTED_TOTAL = 102;
    private static final String EXTERNAL_DB_ID = "base1-1";
    private static final String CARD_SET = "Base Set";
    private static final Float MARKET_PRICE_NORMAL = 10.5f;
    private static final Float MARKET_PRICE_HOLO = 25.0f;
    private static final Float MARKET_PRICE_REVERSE_HOLO = 15.75f;
    private static final String CARD_IMAGE_LOW = "low.jpg";
    private static final String CARD_IMAGE_HIGH = "high.jpg";

    private CardConverter cardConverter;
    private Card card;

    @BeforeEach
    void setUp() {
        cardConverter = new CardConverter();
        card = new Card();
        card.setId(ID);
        card.setName(CARD_NAME);
        card.setCardNumber(CARD_NUMBER);
        card.setSetOfficialPrintedTotal(SET_PRINTED_TOTAL);
        card.setExternalDbId(EXTERNAL_DB_ID);
        card.setCardSet(CARD_SET);
        card.setMarketPriceNormal(MARKET_PRICE_NORMAL);
        card.setMarketPriceHolo(MARKET_PRICE_HOLO);
        card.setMarketPriceReverseHolo(MARKET_PRICE_REVERSE_HOLO);
        card.setImageUrlLow(CARD_IMAGE_LOW);
        card.setImageUrlHigh(CARD_IMAGE_HIGH);
    }

    @Test
    void testToCardSearchResult_Success() {
        CardSearchResult result = cardConverter.toCardSearchResult(card);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(CARD_NAME, result.getName());
        assertEquals(CARD_NUMBER, result.getCardNumber());
        assertEquals(SET_PRINTED_TOTAL, result.getSetPrintedTotal());
        assertEquals(EXTERNAL_DB_ID, result.getExternalDbId());
        assertEquals(CARD_SET, result.getCardSet());
        assertEquals(MARKET_PRICE_NORMAL, result.getMarketPriceNormal());
        assertEquals(MARKET_PRICE_HOLO, result.getMarketPriceHolo());
        assertEquals(MARKET_PRICE_REVERSE_HOLO, result.getMarketPriceReverseHolo());
        assertEquals(CARD_IMAGE_LOW, result.getImageUrlLow());
        assertEquals(CARD_IMAGE_HIGH, result.getImageUrlHigh());
    }

    @Test
    void testToCardSearchResult_NullInput() {
        CardSearchResult result = cardConverter.toCardSearchResult(null);

        assertNull(result);
    }

    @Test
    void testToCardSearchResult_NullFields() {
        Card nullCard = new Card();
        nullCard.setId(ID);
        nullCard.setName(CARD_NAME);

        CardSearchResult result = cardConverter.toCardSearchResult(nullCard);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(CARD_NAME, result.getName());
        assertNull(result.getCardNumber());
        assertNull(result.getExternalDbId());
        assertNull(result.getCardSet());
        assertNull(result.getMarketPriceNormal());
        assertNull(result.getMarketPriceHolo());
        assertNull(result.getMarketPriceReverseHolo());
        assertNull(result.getImageUrlLow());
        assertNull(result.getImageUrlHigh());
    }
}
