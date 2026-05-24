package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.model.PokemonTCGResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CardLoaderService {
    private static final String pokemonAPIURL = "https://api.pokemontcg.io/v2/cards?page=1&pageSize=2";
    private final RestTemplate restTemplate;

    public PokemonTCGResponse getCardTest() {
        return restTemplate.getForObject(pokemonAPIURL, PokemonTCGResponse.class);
    }
}
