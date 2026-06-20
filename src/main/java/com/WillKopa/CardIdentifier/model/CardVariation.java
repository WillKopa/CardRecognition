package com.WillKopa.CardIdentifier.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CardVariation", description = "The cardVariation of the card")
public enum CardVariation {
    NORMAL,
    HOLOGRAPHIC,
    REVERSE_HOLOGRAPHIC,
    SPECIAL
}
