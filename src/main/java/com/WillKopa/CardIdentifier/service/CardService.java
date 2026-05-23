package com.WillKopa.CardIdentifier.service;

import ai.onnxruntime.OrtException;
import com.WillKopa.CardIdentifier.converter.VectorConverter;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@AllArgsConstructor
public class CardService {
    // 40.5f seems to work. Only tested on 3 cards so far.
    private static final float matchThreshold = 40.5f;
    private final EmbeddingService embeddingService;
    private CardRepo cardRepo;

    public Card identifyCard(MultipartFile imageFile) throws Exception {
        String vectorString = toVectorString(imageFile);
        Card match = cardRepo.identifyCard(vectorString, matchThreshold);

        if (match == null) {
            throw new RuntimeException("No match found");
        }
        match.setImageEmbedding(new float[0]);
        return match;
    }

    public void loadCard(Card card, MultipartFile imageFile) throws IOException, OrtException {
        cardRepo.saveWithEmbedding(card.getGame(),
                card.getName(),
                card.getCardSet(),
                toVectorString(imageFile),
                card.getLastSoldPrice(),
                card.getLastUpdate());
    }

    private String toVectorString(MultipartFile imageFile) throws IOException, OrtException {
        return VectorConverter.embeddingToString(imageToEmbeddings(imageFile));
    }
    private float[] imageToEmbeddings(MultipartFile imageFile) throws IOException, OrtException {
        BufferedImage image = ImageIO.read(imageFile.getInputStream());
        System.out.println("Buffered Image done.");
        return embeddingService.embed(image);
    }
}
