package com.WillKopa.CardIdentifier.service;

import ai.onnxruntime.OrtException;
import com.WillKopa.CardIdentifier.converter.VectorConverter;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.PokemonTCGResponse;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardLoaderService {
    private static final String POKEMON_API_URL = "https://api.pokemontcg.io/v2/cards?page=%d&pageSize=%d";
    private static final String POKEMON = "Pokemon tcg";
    @Value("${pokemon.api.key}")
    private String apiKey;
    private static final int PAGE_SIZE = 250;
    private static final int TIMEOUT = 3;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final EmbeddingService embeddingService;
    private CardRepo cardRepo;

    public void loadPokemon(int startPage) {
        String url = String.format(POKEMON_API_URL, startPage, PAGE_SIZE);
        PokemonTCGResponse response = getCards(url);
        while(!response.getData().isEmpty()) {
            log.info("Loading Pokemon from url: {}", url);
            // Parse data from response and save it to a new Card object.
            for (PokemonTCGResponse.CardData cardData : response.getData()) {
                log.info("Loading new Pokemon: {}: {}", cardData.getId(), cardData.getName());
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
                        continue;
                    }
                } else {
                    continue;
                }

                if (cardData.getTcgplayer() != null) {
                    card.setPriceTypes(MAPPER.writeValueAsString(cardData.getTcgplayer().getPrices()));
                    card.setLastUpdate(cardData.getTcgplayer().getUpdatedAt());
                } else {
                    card.setPriceTypes(null);
                    card.setLastUpdate(new Date());
                }


                cardRepo.saveWithEmbedding(
                        card.getExternalDbId(),
                        card.getGame(),
                        card.getName(),
                        card.getCardSet(),
                        card.getCardSetId(),
                        VectorConverter.embeddingToString(card.getImageEmbedding()),
                        card.getPriceTypes(),
                        card.getLastUpdate()
                );
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
            response = getCards(url);
        }

        log.info("Finished loading from: {}", url);
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

    private HttpEntity<Void> getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        return new HttpEntity<>(headers);
    }

    private PokemonTCGResponse getCards(String url) {
        ResponseEntity<PokemonTCGResponse> response = restTemplate.exchange(url,
                HttpMethod.GET,
                getRequestEntity(),
                PokemonTCGResponse.class);
        return response.getBody();
    }
}
