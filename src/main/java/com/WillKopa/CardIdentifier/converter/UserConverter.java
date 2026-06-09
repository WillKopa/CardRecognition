package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.dto.response.UserResponse;
import com.WillKopa.CardIdentifier.dto.response.CardResponse;
import com.WillKopa.CardIdentifier.model.User;

import java.util.List;

public class UserConverter {
    public static UserResponse toResponse (User user) {
        List<CardResponse> cards = user.getCardList().stream()
                .map(userCard ->
                        new CardResponse(
                                userCard.getCard().getId(),
                                userCard.getQuantity(),
                                userCard.getCard().getName(),
                                userCard.getCard().getExternalDbId(),
                                userCard.getCard().getCardSet(),
                                userCard.getCard().getCardSetId()
                        )).toList();
        return new UserResponse(
                user.getUserName(),
                user.getCollectionValue(),
                cards
        );
    }
}
