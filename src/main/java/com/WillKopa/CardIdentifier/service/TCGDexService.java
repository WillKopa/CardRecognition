package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TCGDexService {
    private final TCGdex tcGdex = new TCGdex("en");

    public net.tcgdex.sdk.models.Card getCard(String cardId) {
        return tcGdex.fetchCard(cardId);
    }

    public void getCardPriceAndImageURL(CardSearchResult card) {
        Optional<Card> responseOptional = Optional.ofNullable(getCard(card.getExternalDbId()));
        Optional<PricingTcgPlayer> tcgPlayerOptional = responseOptional
                .map(net.tcgdex.sdk.models.Card::getPricing)
                .map(Pricing::getTcgplayer);

        card.setImageURL(responseOptional.map(
                r -> r.getImageUrl(Quality.HIGH, Extension.WEBP)).orElse(null)
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
}
