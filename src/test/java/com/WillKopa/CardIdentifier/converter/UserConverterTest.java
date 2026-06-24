package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.dto.response.CardResponse;
import com.WillKopa.CardIdentifier.dto.response.UserResponse;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardCondition;
import com.WillKopa.CardIdentifier.model.CardVariation;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.model.UserCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserConverterTest {
    private static final Integer ID = 1;
    private static final String EMAIL = "test@example.com";
    private static final String USERNAME = "testuser";
    private static final BigDecimal COLLECTION_VALUE = new BigDecimal("21.00");
    private static final String CARD_NAME = "Pikachu";
    private static final String CARD_NUMBER = "001";
    private static final Integer SET_PRINTED_TOTAL = 102;
    private static final String EXTERNAL_DB_ID = "base1-1";
    private static final String CARD_SET = "Base Set";
    private static final Float MARKET_PRICE_NORMAL = 10.5f;
    private static final Float MARKET_PRICE_HOLOGRAPHIC = 25.0f;
    private static final Float MARKET_PRICE_REVERSE_HOLOGRAPHIC = 15.75f;
    private static final String CARD_SET_ID = "base1";
    private static final String IMAGE_URL_LOW = "low.jpg";
    private static final String IMAGE_URL_HIGH = "high.jpg";
    private static final CardVariation CARD_VARIATION = CardVariation.NORMAL;
    private static final CardCondition CARD_CONDITION = CardCondition.NEAR_MINT;
    private static final Integer QUANTITY = 2;

    private User user;
    private Card card;
    private UserCard userCard;

    @BeforeEach
    void setUp() {
        card = new Card();
        card.setId(ID);
        card.setName(CARD_NAME);
        card.setCardNumber(CARD_NUMBER);
        card.setSetOfficialPrintedTotal(SET_PRINTED_TOTAL);
        card.setExternalDbId(EXTERNAL_DB_ID);
        card.setCardSet(CARD_SET);
        card.setCardSetId(CARD_SET_ID);
        card.setMarketPriceNormal(MARKET_PRICE_NORMAL);
        card.setMarketPriceHolo(MARKET_PRICE_HOLOGRAPHIC);
        card.setMarketPriceReverseHolo(MARKET_PRICE_REVERSE_HOLOGRAPHIC);
        card.setImageUrlLow(IMAGE_URL_LOW);
        card.setImageUrlHigh(IMAGE_URL_HIGH);

        userCard = UserCard.builder()
            .card(card)
            .quantity(QUANTITY)
            .cardCondition(CARD_CONDITION)
            .cardVariation(CARD_VARIATION)
            .build();

        List<UserCard> cardList = new ArrayList<>();
        cardList.add(userCard);

        user = User.builder()
            .id(ID)
            .email(EMAIL)
            .userName(USERNAME)
            .collectionValue(COLLECTION_VALUE)
            .cardList(cardList)
            .build();
    }

    @Test
    void testToResponse_Success() {
        UserResponse response = UserConverter.toResponse(user);

        assertNotNull(response);
        assertEquals(USERNAME, response.userName());
        assertEquals(COLLECTION_VALUE, response.collectionValue());
        assertNotNull(response.cards());
        assertEquals(user.getCardList().size(), response.cards().size());
    }

    @Test
    void testToResponse_CardDetails() {
        UserResponse response = UserConverter.toResponse(user);

        CardResponse cardResponse = response.cards().getFirst();
        assertEquals(ID, cardResponse.id());
        assertEquals(QUANTITY, cardResponse.count());
        assertEquals(CARD_NAME, cardResponse.name());
        assertEquals(CARD_NUMBER, cardResponse.cardNumber());
        assertEquals(SET_PRINTED_TOTAL, cardResponse.setOfficialPrintedTotal());
        assertEquals(EXTERNAL_DB_ID, cardResponse.externalDbId());
        assertEquals(CARD_SET, cardResponse.cardSet());
        assertEquals(CARD_SET_ID, cardResponse.cardSetId());
        assertEquals(IMAGE_URL_LOW, cardResponse.image_url_low());
        assertEquals(IMAGE_URL_HIGH, cardResponse.image_url_high());
        assertEquals(CARD_VARIATION, cardResponse.cardVariation());
        assertEquals(CARD_CONDITION, cardResponse.cardCondition());
    }

    @Test
    void testToResponse_MarketPriceNormal() {
        userCard.setCardVariation(CardVariation.NORMAL);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(MARKET_PRICE_NORMAL, response.cards().getFirst().marketPrice());
    }

    @Test
    void testToResponse_MarketPriceHolo() {
        userCard.setCardVariation(CardVariation.HOLOGRAPHIC);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(MARKET_PRICE_HOLOGRAPHIC, response.cards().getFirst().marketPrice());
    }

    @Test
    void testToResponse_MarketPriceReverseHolo() {
        userCard.setCardVariation(CardVariation.REVERSE_HOLOGRAPHIC);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(MARKET_PRICE_REVERSE_HOLOGRAPHIC, response.cards().getFirst().marketPrice());
    }

    @Test
    void testToResponse_MarketPriceSpecial() {
        userCard.setCardVariation(CardVariation.SPECIAL);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(0f, response.cards().getFirst().marketPrice());
    }

    @Test
    void testToResponse_EmptyCardList() {
        user.setCardList(new ArrayList<>());

        UserResponse response = UserConverter.toResponse(user);

        assertNotNull(response);
        assertEquals(USERNAME, response.userName());
        assertNotNull(response.cards());
        assertTrue(response.cards().isEmpty());
    }

    @Test
    void testToResponse_MultipleCards() {
        Card card2 = new Card();
        card2.setId(2);
        card2.setName("Charizard");
        card2.setCardNumber("004");
        card2.setSetOfficialPrintedTotal(102);
        card2.setExternalDbId("base1-4");
        card2.setCardSet("Base Set");
        card2.setCardSetId("base1");
        card2.setMarketPriceNormal(100.0f);
        card2.setMarketPriceHolo(250.0f);
        card2.setMarketPriceReverseHolo(150.0f);
        card2.setImageUrlLow("low2.jpg");
        card2.setImageUrlHigh("high2.jpg");

        UserCard userCard2 = UserCard.builder()
            .card(card2)
            .quantity(1)
            .cardCondition(CardCondition.LIGHTLY_PLAYED)
            .cardVariation(CardVariation.HOLOGRAPHIC)
            .build();

        user.getCardList().add(userCard2);

        UserResponse response = UserConverter.toResponse(user);

        assertNotNull(response);
        assertEquals(2, response.cards().size());
        assertEquals(CARD_NAME, response.cards().get(0).name());
        assertEquals("Charizard", response.cards().get(1).name());
        assertEquals(250.0f, response.cards().get(1).marketPrice());
    }
}
