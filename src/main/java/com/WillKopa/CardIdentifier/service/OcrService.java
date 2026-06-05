package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.model.OCRResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class OcrService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${OCR_URL:http://localhost:5000/ocr}")
    private String ocrURL;

    public OCRResult performPokemonOcr(MultipartFile file) throws InvalidImageException, NoOcrResultException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
        } catch (IOException e) {
            log.error("Unable to read image file", e);
            throw new InvalidImageException("Unable to read image");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);


        ResponseEntity<OCRResult> response = restTemplate.postForEntity(
                ocrURL,
                requestEntity,
                OCRResult.class
        );

        if (response.getBody() != null) {
            return response.getBody();
        }

        throw new NoOcrResultException("No result when attempting OCR on image");
    }
}
