package com.WillKopa.CardIdentifier.dto.request;

import com.WillKopa.CardIdentifier.model.CardCondition;
import com.WillKopa.CardIdentifier.model.CardVariation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Request DTO for adding or removing cards from a user's collection.
 * <p>
 * Contains the card ID and market value information needed to update
 * a user's card collection.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
public class CardCollectionRequest {
    /** The ID of the card to add or remove */
    private Integer cardId;
    /** The market value of the card for collection value calculation */
    private Float marketValue;
    /** The cardCondition of the card */
    private CardCondition cardCondition;
    /** The cardVariation of the card */
    private CardVariation cardVariation;
}
