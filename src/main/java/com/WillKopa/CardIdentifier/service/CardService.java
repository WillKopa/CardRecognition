package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.model.CardSearchResult;
import com.WillKopa.CardIdentifier.model.OCRResult;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CardService {
    private CardRepo cardRepo;
    private final OcrService ocrService;

    public CardSearchResult identifyCard(MultipartFile imageFile) throws InvalidImageException, NoOcrResultException {
        OCRResult result = ocrService.performPokemonOcr(imageFile);
        log.info("OCR Response {}", result);
        return cardRepo.getCardsByNameAndCardSetConcat(result.getName() + "%", result.getCardNumber(), result.getSetPrintedTotal());
    }
}
