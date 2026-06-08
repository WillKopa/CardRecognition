package com.WillKopa.CardIdentifier.loader;

import com.WillKopa.CardIdentifier.service.CardLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("load-pokemon")
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {
    private final CardLoaderService cardLoaderService;

    @Override
    public void run(String... args) throws Exception {
        cardLoaderService.loadPokemon();
    }
}
