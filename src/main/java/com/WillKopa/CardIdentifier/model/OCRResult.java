package com.WillKopa.CardIdentifier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for OCR processing results.
 * <p>
 * Contains the extracted card information from OCR processing,
 * including the card name, card number, and set printed total.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OCRResult {
    /** The name of the card extracted from OCR */
    private String name;
    /** The card number extracted from OCR */
    private String cardNumber;
    /** The set printed total extracted from OCR */
    private String setPrintedTotal;
}
