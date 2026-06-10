package com.WillKopa.CardIdentifier.exception;

/**
 * Exception thrown when a user already exists in the database.
 * <p>
 * This exception is used when a user attempts to create a new account
 * with an email address that is already associated with an existing user.
 * </p>
 *
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {}
}
