package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.tcgdex.sdk.Extension;
import net.tcgdex.sdk.Quality;
import net.tcgdex.sdk.TCGdex;
import net.tcgdex.sdk.models.Card;
import net.tcgdex.sdk.models.Pricing;
import net.tcgdex.sdk.models.subs.PricingTcgPlayer;
import net.tcgdex.sdk.models.subs.PricingTcgPlayerVariant;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service for interacting with the TCGDex API.
 * <p>
 * Provides methods to fetch card details and pricing information
 * from the TCGDex external API.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TCGDexService {
    private final CardRepo cardRepo;
    private final TCGdex tcGdex = new TCGdex("en");

    /**
     * Fetches a card from the TCGDex API by its ID.
     *
     * @param cardId the external database ID of the card
     * @return the card details from TCGDex, or null if not found
     */
    public net.tcgdex.sdk.models.Card getCard(String cardId) {
        return tcGdex.fetchCard(cardId);
    }

    /**
     * Retrieves pricing information and image URL for a card from TCGDex.
     * <p>
     * Fetches the card details and extracts market prices for different
     * card variants (normal, holo foil, reverse holo foil) and the high-quality
     * image URL.
     * </p>
     *
     * @param card the card search result to update with pricing and image information
     */
    public void getCardPriceAndImageURL(CardSearchResult card) {
        Optional<Card> responseOptional = Optional.ofNullable(getCard(card.getExternalDbId()));
        Optional<PricingTcgPlayer> tcgPlayerOptional = responseOptional
                .map(net.tcgdex.sdk.models.Card::getPricing)
                .map(Pricing::getTcgplayer);

        card.setImageUrlHigh(responseOptional.map(
                r -> r.getImageUrl(Quality.HIGH, Extension.WEBP)).orElse(null)
        );
        card.setImageUrlLow(responseOptional.map(
                r -> r.getImageUrl(Quality.LOW, Extension.WEBP)).orElse(null)
        );

        card.setMarketPriceNormal(tcgPlayerOptional
                .map(PricingTcgPlayer::getNormal)
                .map(PricingTcgPlayerVariant::getMarketPrice)
                .orElse(null)
        );

        card.setMarketPriceHolo(tcgPlayerOptional
                .map(PricingTcgPlayer::getHoloFoil)
                .map(PricingTcgPlayerVariant::getMarketPrice)
                .orElse(null)
        );

        card.setMarketPriceReverseHolo(tcgPlayerOptional
                .map(PricingTcgPlayer::getReverseHolofoil)
                .map(PricingTcgPlayerVariant::getMarketPrice)
                .orElse(null)
        );
    }

    /**
     * Updates a card's set information from the TCGDex API.
     * <p>
     * Fetches detailed card information including set name, set ID, and official
     * printed total, then updates the card in the database.
     * </p>
     *
     * @param result the card search result containing the external database ID
     */
    public void updateCard(CardSearchResult result) {
        getCardPriceAndImageURL(result);
        Optional.ofNullable(tcGdex.fetchCard(result.getExternalDbId())).ifPresent(card -> {
                    com.WillKopa.CardIdentifier.model.Card updatedCard = new com.WillKopa.CardIdentifier.model.Card();
                    updatedCard.setId(result.getId());
                    updatedCard.setCardSet(card.getSet().getName());
                    updatedCard.setCardSetId(card.getSet().getId());
                    updatedCard.setSetOfficialPrintedTotal(card.getSet().getCardCount().getOfficial());
                    updatedCard.setImageUrlLow(card.getImageUrl(Quality.LOW, Extension.WEBP));
                    updatedCard.setImageUrlHigh(card.getImageUrl(Quality.HIGH, Extension.WEBP));
                    updatedCard.setMarketPriceNormal(result.getMarketPriceNormal());
                    updatedCard.setMarketPriceHolo(result.getMarketPriceHolo());
                    updatedCard.setMarketPriceReverseHolo(result.getMarketPriceReverseHolo());
                    cardRepo.updateCard(updatedCard);
                    result.setCardSet(updatedCard.getCardSet());
                }
        );
    }
}
