package com.WillKopa.CardIdentifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Card Identifier service.
 * <p>
 * This Spring Boot application provides REST endpoints for identifying Pokémon cards
 * from images and managing user card collections. It uses OCR technology to extract
 * card information and integrates with external APIs for pricing data.
 * </p>
 */
@SpringBootApplication
public class CardIdentifierApplication {
	static {
		io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;
	}

	/**
	 * Main entry point for the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CardIdentifierApplication.class, args);
	}

}
