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


/**
 * Service for card identification and management.
 * <p>
 * Provides methods for identifying Pokémon cards from images using OCR,
 * fetching card details from the database, and retrieving pricing information
 * from external APIs.
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class CardService {
    private CardRepo cardRepo;
    private final OcrService ocrService;
    private final TCGDexService TCGDexService;
    private final CardLoaderService cardLoaderService;
    private final CardConverter cardConverter;

    /**
     * Identifies a Pokémon card from an uploaded image file.
     * <p>
     * This method performs OCR on the image to extract card details, searches the database
     * for matching cards, updates card information if necessary, and retrieves additional
     * data such as price and image URL from external sources.
     *
     * @param imageFile the cropped and rotated image file containing the Pokémon card to identify
     * @return CardSearchResult containing the identified card's details including price and image URL
     * @throws InvalidImageException if the uploaded image is invalid or cannot be processed
     * @throws NoOcrResultException if OCR fails to extract card information from the image
     */
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
