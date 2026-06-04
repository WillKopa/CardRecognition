package com.WillKopa.CardIdentifier.controller;

import ai.onnxruntime.OrtException;
import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardSearchResult;
import com.WillKopa.CardIdentifier.service.CardService;
import com.WillKopa.CardIdentifier.service.OcrService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CardController {

    private CardService cardService;


    @PostMapping("/identify")
    public ResponseEntity<?> identifyCard(@RequestParam MultipartFile imageFile) {
        log.info("Received request");
//        CardSearchResult result;
        List<String> result;
        try {
            result = cardService.identifyCard(imageFile);
            if (result == null) {
                return new ResponseEntity<>("Unable to read image", HttpStatus.BAD_REQUEST);
            }
//            log.info("Scanned\nName: {}\nSet: {}\nNumber: {}", result.getName(), result.getCardSet(), result.getCardNumber());
        } catch (InvalidImageException | NoOcrResultException e) {
            return new ResponseEntity<>("Unable to read image", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
//        try {
//            return new ResponseEntity<>(cardService.identifyCard(imageFile), HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
    }

    //Adding to test connection from phone
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}
