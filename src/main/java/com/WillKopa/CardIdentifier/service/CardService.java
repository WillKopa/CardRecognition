package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import lombok.AllArgsConstructor;
import org.bytedeco.opencv.global.opencv_img_hash;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class CardService {
    private CardRepo cardRepo;

    public Card identifyCard(MultipartFile imageFile) throws IOException {
        byte[] bytes = imageFile.getBytes();
        Mat image = opencv_imgcodecs.imdecode(new Mat(bytes), opencv_imgcodecs.IMREAD_COLOR);

        if (image.empty()) {
            throw new IllegalArgumentException("JavaCV could not decode the uploaded image.");
        }

        Mat hashMat = new Mat();

        opencv_img_hash.pHash(image, hashMat);

        BigInteger cardHash = matToBigInteger(hashMat);

        image.release();
        hashMat.release();

        int maxBitDifferenceThreshold = 10;
        return cardRepo.identifyCard(cardHash, maxBitDifferenceThreshold);
    }

    private BigInteger matToBigInteger(Mat hashMat) {
        byte[] hashBytes = new byte[8];
        hashMat.data().get(hashBytes);
        return new BigInteger(1, hashBytes);
    }
}
