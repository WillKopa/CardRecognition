package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.dto.response.UserResponse;
import com.WillKopa.CardIdentifier.dto.response.CardResponse;
import com.WillKopa.CardIdentifier.model.User;

import java.util.List;

/**
 * Converter class for transforming User entities to UserResponse DTOs.
 * <p>
 * Provides static methods to convert between internal User entities and external
 * UserResponse response objects used in API responses, including nested card collections.
 * </p>
 */
public class UserConverter {
    /**
     * Converts a User entity to a UserResponse DTO.
     * <p>
     * Transforms the user's card collection into a list of CardResponse objects,
     * including quantity information for each card.
     * </p>
     *
     * @param user the User entity to convert
     * @return UserResponse DTO with user details and card collection
     */
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
