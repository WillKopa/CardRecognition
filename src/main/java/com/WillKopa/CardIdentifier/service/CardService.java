package com.WillKopa.CardIdentifier.service;

import ai.onnxruntime.OrtException;
import com.WillKopa.CardIdentifier.converter.VectorConverter;
import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.internal.OCR;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardSearchResult;
import com.WillKopa.CardIdentifier.model.OCRResult;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class CardService {
    // 40.5f seems to work. Only tested on 3 cards so far.
    private static final float matchThreshold = 40.5f;
    private final EmbeddingService embeddingService;
    private CardRepo cardRepo;

    public CardSearchResult identifyCard(String savePath) throws InvalidImageException {
//        String vectorString = toVectorString(imageFile);
//        Card match = cardRepo.identifyCard(vectorString, matchThreshold);
//
//        if (match == null) {
//            throw new RuntimeException("No match found");
//        }
//        match.setImageEmbedding(new float[0]);

        OCRResult result = OCR.identifyPokemonCard(savePath);

        return cardRepo.getCardsByNameAndCardSetConcat(result.getName(), result.getCardNumberConcat());
    }

    private String toVectorString(MultipartFile imageFile) throws IOException, OrtException {
        return VectorConverter.embeddingToString(embeddingService.imageToEmbeddings(imageFile));
    }
}
