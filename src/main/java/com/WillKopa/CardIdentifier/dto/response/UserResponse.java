package com.WillKopa.CardIdentifier.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for user information including card collection.
 * <p>
 * Contains the user's name, total collection value, and a list of cards
 * in their collection with quantity information.
 * </p>
 */
public record UserResponse (
        /** The user's display name */
        String userName,
        /** The total value of the user's card collection */
        BigDecimal collectionValue,
        /** List of cards in the user's collection */
        List<CardResponse> cards
        ) {
}
