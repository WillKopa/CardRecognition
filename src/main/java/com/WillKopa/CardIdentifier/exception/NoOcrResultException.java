package com.WillKopa.CardIdentifier.exception;

/**
 * Exception thrown when OCR processing fails to return a result.
 * <p>
 * This exception is used when the OCR service completes processing
 * but does not return any usable card information from the image.
 * </p>
 */
public class NoOcrResultException extends Exception {
    /**
     * Constructs a new NoOcrResultException with the specified detail message.
     *
     * @param message the detail message explaining why OCR failed to return a result
     */
    public NoOcrResultException(String message) {
        super(message);
    }
}
