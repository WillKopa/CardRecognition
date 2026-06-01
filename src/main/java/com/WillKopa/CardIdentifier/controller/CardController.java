package com.WillKopa.CardIdentifier.controller;

import ai.onnxruntime.OrtException;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.service.CardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CardController {

    private CardService cardService;

    @PostMapping("/identify")
    public ResponseEntity<?> identifyCard(@RequestParam MultipartFile imageFile) {
        log.info("Received request");
        try {
            Path savePath = Paths.get("test/" + imageFile.getOriginalFilename());
            Files.createDirectories(savePath.getParent());
            Files.write(savePath, imageFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
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
