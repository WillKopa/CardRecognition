package com.WillKopa.CardIdentifier.controller;

import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.service.CardService;
import com.WillKopa.CardIdentifier.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for card identification and collection management.
 * <p>
 * Provides endpoints for identifying Pokémon cards from images and adding them
 * to user collections. Includes public endpoints for card identification without authentication.
 * </p>
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CardController {

    private CardService cardService;
    private UserService userService;


    /**
     * Identifies a Pokémon card from an uploaded image file.
     * <p>
     * This is a public endpoint that performs OCR on the image and returns
     * card details including name, set, number, and pricing information.
     * </p>
     *
     * @param imageFile the image file containing the cropped and rotated Pokémon card to identify
     * @return ResponseEntity containing the identified card's search result
     * @throws NoOcrResultException if OCR fails to extract card information
     * @throws InvalidImageException if the uploaded image is invalid
     */
    @PostMapping(
            value = "/public/identify",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CardSearchResult> identifyCard(@RequestParam MultipartFile imageFile) throws NoOcrResultException, InvalidImageException {
        log.info("Received request");
        CardSearchResult result = cardService.identifyCard(imageFile);
        log.info("Scanned\nName: {}\nSet: {}\nNumber: {}", result.getName(), result.getCardSet(), result.getCardNumber());
        return ResponseEntity.ok(result);
    }

    /**
     * Identifies a Pokémon card from an uploaded image and adds it to the authenticated user's collection.
     * <p>
     * Performs OCR on the image, retrieves card details, and automatically adds the card
     * to the user's collection with the current market price.
     * </p>
     *
     * @param imageFile the image file containing the cropped and rotated Pokémon card to identify
     * @param jwt the JWT token containing authentication information
     * @return ResponseEntity containing the updated user with the new card added
     * @throws NoOcrResultException if OCR fails to extract card information
     * @throws InvalidImageException if the uploaded image is invalid
     */
    @PostMapping(
            value = "/identifyAndAddToCollection",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<User> identifyAndAddToCollection(@RequestParam MultipartFile imageFile, @AuthenticationPrincipal Jwt jwt) throws NoOcrResultException, InvalidImageException {
        log.info("Received request");
        CardSearchResult result = cardService.identifyCard(imageFile);
        log.info("Scanned\nName: {}\nSet: {}\nNumber: {}", result.getName(), result.getCardSet(), result.getCardNumber());
        User user = userService.addCard(result.getId(), result.getMarketPriceNormal(), jwt.getClaimAsString("email"));
        return ResponseEntity.ok(user);
    }


    /**
     * Test endpoint to verify API connectivity.
     * <p>
     * Returns a simple greeting message to confirm the service is running.
     * Used for testing connection from mobile devices.
     * </p>
     *
     * @return ResponseEntity containing a greeting message
     */
    //Adding to test connection from phone
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}
