package com.WillKopa.CardIdentifier.controller;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CardController {

    private CardService cardService;

    @PostMapping("/identify")
    public ResponseEntity<?> identifyCard(@RequestParam MultipartFile imageFile) {
        try {
            return new ResponseEntity<>(cardService.identifyCard(imageFile), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/load")
    public ResponseEntity<Card> loadCard(@RequestParam String game, @RequestParam String name, @RequestParam String cardSet, @RequestParam BigInteger lastSoldPrice, @RequestParam MultipartFile imageFile) {
        Card card = new Card();
        card.setGame(game);
        card.setLastSoldPrice(lastSoldPrice);
        card.setCardSet(cardSet);
        card.setName(name);
        try {
            return new ResponseEntity<>(cardService.loadCard(card, imageFile), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
