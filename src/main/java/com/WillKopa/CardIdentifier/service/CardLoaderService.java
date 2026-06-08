package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardSearchResult;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.tcgdex.sdk.TCGdex;
import net.tcgdex.sdk.models.CardResume;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardLoaderService {
    private final CardRepo cardRepo;
    private final TCGdex tcGdex = new TCGdex("en");

    public void updateCard(CardSearchResult result) {
        Optional.ofNullable(tcGdex.fetchCard(result.getExternalDbId())).ifPresent(card -> {
                    Card updatedCard = new Card();
                    updatedCard.setId(result.getId());
                    updatedCard.setCardSet(card.getSet().getName());
                    updatedCard.setCardSetId(card.getSet().getId());
                    updatedCard.setSetOfficialPrintedTotal(card.getSet().getCardCount().getOfficial());
                    cardRepo.updateCard(updatedCard);

                    result.setCardSet(updatedCard.getCardSet());
                }
        );
    }

    public void loadPokemon() {
        final String POKEMON = "Pokemon TCG";
        Optional.ofNullable(tcGdex.fetchCards())
                .ifPresent(allCards -> {
            for (CardResume cardResume : allCards) {
                log.info("Loading Pokemon: {} \nNumber: {}", cardResume.getName(), cardResume.getLocalId());
                // Parse data from response and save it to a new Card object.
                Card card = new Card();
                card.setExternalDbId(cardResume.getId());
                card.setGame(POKEMON);
                card.setName(cardResume.getName());
                card.setCardSet(null);
                card.setCardSetId(null);
                card.setCardNumber(cardResume.getLocalId());
                card.setSetOfficialPrintedTotal(-1);

                cardRepo.save(card);
            }
        });
        log.info("Finished loading from card");
    }
}
