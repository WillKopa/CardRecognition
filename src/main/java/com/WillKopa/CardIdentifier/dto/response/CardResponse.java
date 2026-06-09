package com.WillKopa.CardIdentifier.dto.response;

public record CardResponse(
        Integer id,
        Integer count,
        String name,
        String externalDbId,
        String cardSet,
        String cardSetId
) {}
