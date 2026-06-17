package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.dto.response.UserResponse;
import com.WillKopa.CardIdentifier.dto.response.CardResponse;
import com.WillKopa.CardIdentifier.model.CardVariation;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.model.UserCard;

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
                                userCard.getCard().getCardSetId(),
                                userCard.getCard().getImageUrlLow(),
                                userCard.getCard().getImageUrlHigh(),
                                userCard.getCardVariation(),
                                userCard.getCardCondition(),
                                getMarketPrice(userCard)
                                )).toList();
        return new UserResponse(
                user.getUserName(),
                user.getCollectionValue(),
                cards
        );
    }

    private static Float getMarketPrice(UserCard userCard) {
        return switch (userCard.getCardVariation()) {
            case CardVariation.NORMAL -> userCard.getCard().getMarketPriceNormal();
            case CardVariation.HOLOGRAPHIC -> userCard.getCard().getMarketPriceHolo();
            case CardVariation.REVERSE_HOLOGRAPHIC -> userCard.getCard().getMarketPriceReverseHolo();
            default -> 0f;
        };
    }
}
