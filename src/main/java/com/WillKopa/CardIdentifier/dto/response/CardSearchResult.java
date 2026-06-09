package com.WillKopa.CardIdentifier.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class CardSearchResult {
    private Integer id;
    private String name;
    private String cardNumber;
    private String externalDbId;
    private String cardSet;
    private Float marketPriceNormal;
    private Float marketPriceHolo;
    private Float marketPriceReverseHolo;
    private String imageURL;
}
