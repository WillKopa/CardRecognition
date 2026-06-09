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

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CardController {

    private CardService cardService;
    private UserService userService;


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


    //Adding to test connection from phone
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}
