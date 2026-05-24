package com.WillKopa.CardIdentifier.controller;

import com.WillKopa.CardIdentifier.service.CardLoaderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CardLoaderController {
    CardLoaderService cardLoaderService;

    @GetMapping("/testLoading")
    public ResponseEntity<?> testCardLoading() {
        try {
            return new ResponseEntity<>(cardLoaderService.getCardTest(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
