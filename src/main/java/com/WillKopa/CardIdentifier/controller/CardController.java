package com.WillKopa.CardIdentifier.controller;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@RestController("/api")
public class CardController {

    private CardService cardService;

    @PostMapping("/identify")
    public ResponseEntity<Card> identifyCard(@RequestBody MultipartFile imageFile) {
        try {
            return new ResponseEntity<>(cardService.identifyCard(imageFile), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
