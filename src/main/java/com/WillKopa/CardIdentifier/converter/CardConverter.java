package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import org.springframework.stereotype.Component;

/**
 * Converter class for transforming Card entities to CardSearchResult DTOs.
 * <p>
 * Provides methods to convert between internal Card entities and external
 * CardSearchResult response objects used in API responses.
 * </p>
 */
@Component
public class CardConverter {
    /**
     * Converts a Card entity to a CardSearchResult DTO.
     *
     * @param card the Card entity to convert, may be null
     * @return CardSearchResult DTO with card details, or null if input is null
     */
    public CardSearchResult toCardSearchResult(Card card) {
        if (card == null) {
            return null;
        }

        return new CardSearchResult(
                card.getId(),
                card.getName(),
                card.getCardNumber(),
                card.getSetOfficialPrintedTotal(),
                card.getExternalDbId(),
                card.getCardSet(),
                card.getMarketPriceNormal(),
                card.getMarketPriceHolo(),
                card.getMarketPriceReverseHolo(),
                card.getImageUrlLow(),
                card.getImageUrlHigh() 
        );
    }
}
