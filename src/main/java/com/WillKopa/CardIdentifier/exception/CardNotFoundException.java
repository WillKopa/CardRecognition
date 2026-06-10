package com.WillKopa.CardIdentifier.exception;

/**
 * Exception thrown when a card cannot be found in the database.
 * <p>
 * This exception is used when attempting to access or modify a card
 * that does not exist in the system.
 * </p>
 */
public class CardNotFoundException extends RuntimeException {
    /**
     * Constructs a new CardNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the card was not found
     */
    public CardNotFoundException(String message) {
        super(message);
    }
}
