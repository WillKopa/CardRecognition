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
        int startPage = 1;

        if (args.length > 0) {
            startPage = Integer.parseInt(args[0]);
        }

        cardLoaderService.loadPokemon(startPage);
    }
}
