package com.WillKopa.CardIdentifier.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardCollectionRequest {
    private Integer cardId;
    private Float marketValue;
}
