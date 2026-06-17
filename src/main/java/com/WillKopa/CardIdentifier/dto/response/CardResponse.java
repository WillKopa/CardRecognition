package com.WillKopa.CardIdentifier.dto.response;

import com.WillKopa.CardIdentifier.model.CardCondition;
import com.WillKopa.CardIdentifier.model.CardVariation;

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
        /** The card number of the card */
        String cardNumber,
        /** The official printed total for the set */
        int setOfficialPrintedTotal,
        /** The external database ID of the card */
        String externalDbId,
        /** The set name of the card */
        String cardSet,
        /** The set ID of the card */
        String cardSetId,
        /** url to low res image **/
        String image_url_low,
        /** url to high res image **/
        String image_url_high,
        /** The condition of the card **/
        CardVariation variation,
        /** The variation of the card **/
        CardCondition condition,
        /** The market price of the card **/
        Float marketPrice
) {}
