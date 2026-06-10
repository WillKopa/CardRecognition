package com.WillKopa.CardIdentifier.exception;

/**
 * Exception thrown when a user does not exist in the database.
 * <p>
 * This exception is used when an attempt to access to a user that does not exist occurs.
 * </p>
 *
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
