package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.converter.CardConverter;
import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import com.WillKopa.CardIdentifier.model.OCRResult;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@AllArgsConstructor
public class CardService {
    private CardRepo cardRepo;
    private final OcrService ocrService;
    private final TCGDexService TCGDexService;
    private final CardLoaderService cardLoaderService;
    private final CardConverter cardConverter;

    public CardSearchResult identifyCard(MultipartFile imageFile) throws InvalidImageException, NoOcrResultException {
        OCRResult result = ocrService.performPokemonOcr(imageFile);
        log.info("OCR Response {}", result);
        CardSearchResult card = cardConverter.toCardSearchResult(cardRepo.getCardsByNameAndNumber("%" + result.getName() + "%", result.getCardNumber()));

        log.info("Retrieved card from DB: {}", card);

        if (card.getCardSet() == null) {
            log.info("Updating card: {}, ID: {}", card.getName(), card.getId());
            cardLoaderService.updateCard(card);
        }

        System.out.println("Name: " + card.getName() +
                "\nNumber: " + card.getCardNumber() +
                "\nPrinted total: " + card.getCardSet() +
                "\nExternal Id: " + card.getExternalDbId());
        TCGDexService.getCardPriceAndImageURL(card);

        return card;
    }
}
