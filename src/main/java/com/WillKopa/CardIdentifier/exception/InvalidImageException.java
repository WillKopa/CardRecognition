package com.WillKopa.CardIdentifier.exception;

/**
 * Exception thrown when an uploaded image file is invalid or cannot be processed.
 * <p>
 * This exception is used when the OCR service cannot read or process
 * the provided image file due to format issues or corruption.
 * </p>
 */
public class InvalidImageException extends Exception {
    /**
     * Constructs a new InvalidImageException with the specified detail message.
     *
     * @param message the detail message explaining why the image is invalid
     */
    public InvalidImageException(String message) {
        super(message);
    }
}
