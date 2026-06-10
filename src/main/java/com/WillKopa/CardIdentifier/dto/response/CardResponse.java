package com.WillKopa.CardIdentifier.dto.response;

/**
 * Response DTO for card information in a user's collection.
 * <p>
 * Contains card details including ID, quantity, name, external database ID,
 * set information, and set ID.
 * </p>
 */
public record CardResponse(
        /** The unique ID of the card */
        Integer id,
        /** The quantity of this card in the user's collection */
        Integer count,
        /** The name of the card */
        String name,
        /** The external database ID of the card */
        String externalDbId,
        /** The set name of the card */
        String cardSet,
        /** The set ID of the card */
        String cardSetId
) {}
