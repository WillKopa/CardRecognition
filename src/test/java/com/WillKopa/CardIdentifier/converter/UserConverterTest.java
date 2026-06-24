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

    private User user;
    private Card card;
    private UserCard userCard;

    @BeforeEach
    void setUp() {
        card = new Card();
        card.setId(1);
        card.setName("Pikachu");
        card.setCardNumber("001");
        card.setSetOfficialPrintedTotal(102);
        card.setExternalDbId("base1-1");
        card.setCardSet("Base Set");
        card.setCardSetId("base1");
        card.setMarketPriceNormal(10.5f);
        card.setMarketPriceHolo(25.0f);
        card.setMarketPriceReverseHolo(15.75f);
        card.setImageUrlLow("low.jpg");
        card.setImageUrlHigh("high.jpg");

        userCard = UserCard.builder()
            .card(card)
            .quantity(2)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();

        List<UserCard> cardList = new ArrayList<>();
        cardList.add(userCard);

        user = User.builder()
            .id(1)
            .email("test@example.com")
            .userName("testuser")
            .collectionValue(new BigDecimal("21.00"))
            .cardList(cardList)
            .build();
    }

    @Test
    void testToResponse_Success() {
        UserResponse response = UserConverter.toResponse(user);

        assertNotNull(response);
        assertEquals("testuser", response.userName());
        assertEquals(new BigDecimal("21.00"), response.collectionValue());
        assertNotNull(response.cards());
        assertEquals(1, response.cards().size());
    }

    @Test
    void testToResponse_CardDetails() {
        UserResponse response = UserConverter.toResponse(user);

        CardResponse cardResponse = response.cards().getFirst();
        assertEquals(1, cardResponse.id());
        assertEquals(2, cardResponse.count());
        assertEquals("Pikachu", cardResponse.name());
        assertEquals("001", cardResponse.cardNumber());
        assertEquals(102, cardResponse.setOfficialPrintedTotal());
        assertEquals("base1-1", cardResponse.externalDbId());
        assertEquals("Base Set", cardResponse.cardSet());
        assertEquals("base1", cardResponse.cardSetId());
        assertEquals("low.jpg", cardResponse.image_url_low());
        assertEquals("high.jpg", cardResponse.image_url_high());
        assertEquals(CardVariation.NORMAL, cardResponse.cardVariation());
        assertEquals(CardCondition.NEAR_MINT, cardResponse.cardCondition());
    }

    @Test
    void testToResponse_MarketPriceNormal() {
        userCard.setCardVariation(CardVariation.NORMAL);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(10.5f, response.cards().get(0).marketPrice());
    }

    @Test
    void testToResponse_MarketPriceHolo() {
        userCard.setCardVariation(CardVariation.HOLOGRAPHIC);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(25.0f, response.cards().get(0).marketPrice());
    }

    @Test
    void testToResponse_MarketPriceReverseHolo() {
        userCard.setCardVariation(CardVariation.REVERSE_HOLOGRAPHIC);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(15.75f, response.cards().get(0).marketPrice());
    }

    @Test
    void testToResponse_MarketPriceSpecial() {
        userCard.setCardVariation(CardVariation.SPECIAL);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(0f, response.cards().get(0).marketPrice());
    }

    @Test
    void testToResponse_EmptyCardList() {
        user.setCardList(new ArrayList<>());

        UserResponse response = UserConverter.toResponse(user);

        assertNotNull(response);
        assertEquals("testuser", response.userName());
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
        assertEquals("Pikachu", response.cards().get(0).name());
        assertEquals("Charizard", response.cards().get(1).name());
        assertEquals(250.0f, response.cards().get(1).marketPrice());
    }

    @Test
    void testToResponse_ZeroCollectionValue() {
        user.setCollectionValue(BigDecimal.ZERO);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(BigDecimal.ZERO, response.collectionValue());
    }

    @Test
    void testToResponse_LargeCollectionValue() {
        user.setCollectionValue(new BigDecimal("10000.00"));

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(new BigDecimal("10000.00"), response.collectionValue());
    }

    @Test
    void testToResponse_DifferentConditions() {
        userCard.setCardCondition(CardCondition.HEAVILY_PLAYED);

        UserResponse response = UserConverter.toResponse(user);

        assertEquals(CardCondition.HEAVILY_PLAYED, response.cards().get(0).cardCondition());
    }

    @Test
    void testToResponse_NullPrices() {
        card.setMarketPriceNormal(null);
        card.setMarketPriceHolo(null);
        card.setMarketPriceReverseHolo(null);

        UserResponse response = UserConverter.toResponse(user);

        assertNull(response.cards().get(0).marketPrice());
    }
}
