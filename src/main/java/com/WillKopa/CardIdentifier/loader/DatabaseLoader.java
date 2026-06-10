package com.WillKopa.CardIdentifier.loader;

import com.WillKopa.CardIdentifier.service.CardLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Database loader for initializing card data on application startup.
 * <p>
 * This component runs when the application starts up with the "load-pokemon" profile
 * to load Pokémon card data from the TCGDex API into the database.
 * The application will automatically terminate when loading is completed.
 * </p>
 */
@Component
@Profile("load-pokemon")
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {
    private final CardLoaderService cardLoaderService;

    /**
     * Loads Pokémon card data from the TCGDex API into the database.
     * <p>
     * This method is called automatically on application startup when the
     * "load-pokemon" profile is active.
     * </p>
     *
     * @param args command line arguments (not used)
     * @throws Exception if an error occurs during data loading
     */
    @Override
    public void run(String... args) throws Exception {
        cardLoaderService.loadPokemon();
    }
}
