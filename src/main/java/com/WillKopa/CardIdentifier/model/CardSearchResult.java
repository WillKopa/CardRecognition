package com.WillKopa.CardIdentifier.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Data
public class CardSearchResult {
    private final String name;
    private final String cardSet;
    private final Integer cardNumber;
    private final String externalDbId;
    private Float marketPriceNormal;
    private Float marketPriceHolo;
    private Float marketPriceReverseHolo;
    private String imageURL;
}
