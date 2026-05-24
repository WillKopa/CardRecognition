package com.WillKopa.CardIdentifier.service;

import ai.onnxruntime.OrtException;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.PokemonTCGResponse;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class CardLoaderService {
    private static final String POKEMON_API_URL = "https://api.pokemontcg.io/v2/cards?page=%d&pageSize=%d";
    private static final String POKEMON = "Pokemon tcg";
    private static final int PAGE_SIZE = 250;
    private static final int TIMEOUT = 3;
    private final RestTemplate restTemplate;
    private final EmbeddingService embeddingService;
    private CardRepo cardRepo;

    public void loadPokemon(int startPage) {
        String url = String.format(POKEMON_API_URL, startPage, PAGE_SIZE);
        PokemonTCGResponse response = restTemplate.getForObject(url, PokemonTCGResponse.class);
        while(!response.getData().isEmpty()) {
            // Parse data from response and save it to a new Card object.
            for (PokemonTCGResponse.CardData cardData : response.getData()) {
                Card card = new Card();
                card.setExternalDbId(cardData.getId());
                card.setGame(POKEMON);
                card.setName(cardData.getName());
                card.setCardSet(cardData.getSet().getName());
                card.setCardSetId(cardData.getSet().getId());

                BufferedImage image = getImageFromURL(cardData.getImages().getLarge());
                if (image != null) {
                    try {
                        card.setImageEmbedding(embeddingService.bufferedImageToEmbeddings(image));
                    } catch (OrtException e) {
                        log.error("Error setting embeddings for {} {}", card.getId(), card.getName());
                    }
                }

                card.setPriceTypes(cardData.getTcgplayer().getPrices());
                card.setLastUpdate(cardData.getTcgplayer().getUpdatedAt());

                cardRepo.save(card);
            }

            // Go to next page and pull in new response.
            try {
                TimeUnit.SECONDS.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                log.error("Sleep was interrupted on page {}", startPage, e);
                break;
            }
            startPage++;
            url = String.format(POKEMON_API_URL, startPage, PAGE_SIZE);
            response = restTemplate.getForObject(url, PokemonTCGResponse.class);
        }
    }

    private BufferedImage getImageFromURL(String url) {
        try {
            return ImageIO.read(URI.create(url).toURL());
        } catch (MalformedURLException e) {
            log.error("Malformed URL: ", e);
        } catch (IOException e){
            log.error("Error reading image file: ", e);
        }
        return null;
    }
}
