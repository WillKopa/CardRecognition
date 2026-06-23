package com.WillKopa.CardIdentifier.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


/**
 * Response DTO for card search results from OCR identification.
 * <p>
 * Contains comprehensive card information including identification details,
 * pricing data for different card variants, and image URL.
 * </p>
 */
@AllArgsConstructor
@Data
public class CardSearchResult {
    /** The unique ID of the card */
    private Integer id;
    /** The name of the card */
    private String name;
    /** The card number within its set */
    private String cardNumber;
    /** The "official" number of cards printed in the set */
    private Integer setPrintedTotal;
    /** The external database ID of the card */
    private String externalDbId;
    /** The set name of the card */
    private String cardSet;
    /** The market price for normal variant */
    private Float marketPriceNormal;
    /** The market price for holo foil variant */
    private Float marketPriceHolo;
    /** The market price for reverse holo foil variant */
    private Float marketPriceReverseHolo;
    /** The URL to the low res card image */
    private String imageUrlLow;
    /** The URL to the high res card image */
    private String imageUrlHigh;
}
