package com.WillKopa.CardIdentifier.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardAddRequest {
    private Integer cardId;
    private Float marketValue;
}
