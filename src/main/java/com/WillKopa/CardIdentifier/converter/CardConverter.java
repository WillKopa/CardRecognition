package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardSearchResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class CardConverter {
    public CardSearchResult toCardSearchResult(Card card) {
        if (card == null) {
            return null;
        }

        return new CardSearchResult(
                card.getId(),
                card.getName(),
                card.getCardNumber(),
                card.getExternalDbId(),
                card.getCardSet(),
                null,
                null,
                null,
                null
        );
    }
}
