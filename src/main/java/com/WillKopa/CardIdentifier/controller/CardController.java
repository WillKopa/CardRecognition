package com.WillKopa.CardIdentifier.controller;

import ai.onnxruntime.OrtException;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

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
}
