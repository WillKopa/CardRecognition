package com.WillKopa.CardIdentifier.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record UserResponse (
        String userName,
        BigDecimal collectionValue,
        List<CardResponse> cards
        ) {
}
