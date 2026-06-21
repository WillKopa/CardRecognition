package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.converter.CardConverter;
import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.OCRResult;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


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
    private final TCGDexService tcgDexService;
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
    public List<CardSearchResult> identifyCard(MultipartFile imageFile) throws InvalidImageException, NoOcrResultException {
        OCRResult result = ocrService.performPokemonOcr(imageFile);
        log.info("OCR Response {}", result);
        List<Card> dbResult = cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(
                "%" + result.getName() + "%",
                result.getCardNumber(),
                Integer.parseInt(result.getSetPrintedTotal())
        );

        if (dbResult == null) {
            log.info("Card not found with parsed set number, trying again");
            dbResult = cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(
                    "%" + result.getName() + "%",
                    result.getCardNumber(),
                    -1
            );
        }

        List<CardSearchResult> resultList = new ArrayList<>();

        for (Card card : dbResult) {
            CardSearchResult cardSearchResult = cardConverter.toCardSearchResult(card);
            resultList.add(cardSearchResult);
        }

        log.info("Retrieved {} cards from DB", resultList.size());

        for (CardSearchResult card : resultList) {
            if (card.getCardSet() == null) {
                log.info("Updating card: {}, ID: {}", card.getName(), card.getId());
                tcgDexService.updateCard(card);
            }
            System.out.println("Name: " + card.getName() +
                    "\nNumber: " + card.getCardNumber() +
                    "\nPrinted total: " + card.getCardSet() +
                    "\nExternal Id: " + card.getExternalDbId());
            tcgDexService.getCardPriceAndImageURL(card);
        }

        return resultList;
    }
}
