package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class CardService {
    private CardRepo cardRepo;
    private final OcrService ocrService;

    public List<String> identifyCard(MultipartFile imageFile) throws InvalidImageException, NoOcrResultException {
        List<String> result = ocrService.performPokemonOcr(imageFile);

        System.out.println("Results: " + result);

        return result;

//        return cardRepo.getCardsByNameAndCardSetConcat(result.get(0), result.get(1));
    }
}
