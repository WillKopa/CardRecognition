package com.WillKopa.CardIdentifier.exception;

/**
 * Exception thrown when a card name cannot be found during OCR processing.
 * <p>
 * This exception is used when the OCR service is unable to extract
 * the card name from the provided image.
 * </p>
 */
public class NameNotFoundException extends Exception {
    /**
     * Constructs a new NameNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the name was not found
     */
    public NameNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new NameNotFoundException with no detail message.
     */
    public NameNotFoundException() {}
}
