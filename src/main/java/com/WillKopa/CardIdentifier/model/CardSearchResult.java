package com.WillKopa.CardIdentifier.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Data
public class CardSearchResult {
    private final Integer id;
    private final String name;
    private final String cardNumber;
    private final String externalDbId;
    private String cardSet;
    private Float marketPriceNormal;
    private Float marketPriceHolo;
    private Float marketPriceReverseHolo;
    private String imageURL;
}
