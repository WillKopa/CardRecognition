package com.WillKopa.CardIdentifier.exception;

public class CardNumberNotFoundException extends Exception {
    public CardNumberNotFoundException(String message) {
        super(message);
    }
    public CardNumberNotFoundException() {}
}
