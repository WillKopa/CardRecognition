package com.WillKopa.CardIdentifier.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CardCondition", description = "The cardCondition of the card")
public enum CardCondition {
    NEAR_MINT,
    LIGHTLY_PLAYED,
    MODERATELY_PLAYED,
    HEAVILY_PLAYED,
    DAMAGED
}
