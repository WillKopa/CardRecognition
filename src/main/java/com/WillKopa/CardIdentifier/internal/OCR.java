package com.WillKopa.CardIdentifier.internal;

import com.WillKopa.CardIdentifier.exception.CardNumberNotFoundException;
import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NameNotFoundException;
import com.WillKopa.CardIdentifier.model.OCRResult;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.BytePointer;
import org.springframework.web.multipart.MultipartFile;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_photo.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.addWeighted;
@Slf4j
public class OCR {
    public static OCRResult identifyPokemonCard(String filePath) throws InvalidImageException {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setOcrEngineMode(1);
        BufferedImage image = processImage(filePath);


//        try {
//            image = ImageIO.read(imageFile.getInputStream());
//        } catch (IOException e) {
//            log.error("Error converting received image to BufferedImage", e);
//            throw new InvalidImageException("Image invalid");
//        }

        String name = getPokemonName(image, tesseract);
        String cardNumberConcat = getPokemonCardNumberConcat(image, tesseract);
        return new OCRResult(name, cardNumberConcat);
    }


    public static OCRResult identifyPokemonItemCard(String filePath) throws InvalidImageException {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
        tesseract.setLanguage("eng");
        BufferedImage image = processImage(filePath);


//        try {
//            image = ImageIO.read(imageFile.getInputStream());
//        } catch (IOException e) {
//            log.error("Error converting received image to BufferedImage", e);
//            throw new InvalidImageException("Image invalid");
//        }

        String name = getPokemonItemName(image, tesseract);
        String cardNumberConcat = getPokemonCardNumberConcat(image, tesseract);
        return new OCRResult(name, cardNumberConcat);
    }

    private static String getPokemonName(BufferedImage image, ITesseract tesseract) {
        int width = image.getWidth();
        int height = image.getHeight();
        int x1 = (int) (0.17 * width);
        int y1 = (int) (0.04 * height);
        int x2 = (int) (0.50 * width);
        int y2 = (int) (0.10 * height);

        String result = cropAndOCRImage(image, x1, y1, x2, y2, tesseract);

        try {
            Pattern namePattern = Pattern.compile("\\w+");
            return extractNameFromResult(result, namePattern);
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    };

    private static String getPokemonItemName(BufferedImage image, ITesseract tesseract) {
        int width = image.getWidth();
        int height = image.getHeight();
        int x1 = (int) (0.05 * width);
        int y1 = (int) (0.04 * height);
        int x2 = (int) (0.70 * width);
        int y2 = (int) (0.10 * height);

        String result = cropAndOCRImage(image, x1, y1, x2, y2, tesseract);

        try {
            Pattern namePattern = Pattern.compile("\\b[A-Z]\\w+[\\s-][A-Z]\\w+[\\s-][A-Z]\\w+\\b");
            return extractNameFromResult(result, namePattern);
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    };

    private static String getPokemonCardNumberConcat(BufferedImage image, ITesseract tesseract) {
        int width = image.getWidth();
        int height = image.getHeight();
        int x1 = (int) (0.16 * width);
        int y1 = (int) (0.93 * height);
        int x2 = (int) (0.13 * width);
        int y2 = (int) (0.04 * height);

        String result = cropAndOCRImage(image, x1, y1, x2, y2, tesseract);

        try {
            return extractNumberFromResult(result);
        } catch (CardNumberNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String cropAndOCRImage(BufferedImage image, int x1, int y1, int x2, int y2, ITesseract tesseract) {
        BufferedImage croppedImage =  image.getSubimage(x1, y1, x2, y2);
        try {
            File outputFile = new File("test/cropped_image" + x1 + ".jpg");
            ImageIO.write(croppedImage, "jpg", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return tesseract.doOCR(croppedImage);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractNameFromResult(String result, Pattern pattern) throws NameNotFoundException {
        Matcher nameMatch = pattern.matcher(result);

        if (nameMatch.find()) {
            return nameMatch.group(0);
        }

        throw new NameNotFoundException();
    }

    private static String extractNumberFromResult(String number) throws CardNumberNotFoundException {
        Pattern numberPattern = Pattern.compile("\\d+/\\d+");
        Matcher numberMatcher = numberPattern.matcher(number);

        String match;

        if (numberMatcher.find()) {
            match = numberMatcher.group(0);
            // Strip off leading 0's. Pokemon cards are saved without leading 0's.
            String[] numbers = match.split("/");
            int cardNumber = Integer.parseInt(numbers[0]);
            int printNumber = Integer.parseInt(numbers[1]);
            return cardNumber + "/" + printNumber;
        }

        throw new CardNumberNotFoundException();
    }

    // Turns the MultipartFile into a Mat, then applies gray scale, sharpening, and resizing, then returns as a BufferedImage.
    private static BufferedImage processImage(String filePath) throws InvalidImageException {

        Mat grayImage = imread(filePath, IMREAD_GRAYSCALE);

        Mat sharpened = new Mat();
        GaussianBlur(grayImage, sharpened, new Size(0, 0), 3);
        addWeighted(grayImage, 1.5, sharpened, -0.5, 0, sharpened);


        Mat upscaled = new Mat();
        resize(sharpened, upscaled, new Size(), 2.0, 2.0, INTER_CUBIC);

       return matToBufferedImage(upscaled);
     }

     // Converts a mat to a buffered Image
    public static BufferedImage matToBufferedImage(Mat mat) {
        try (   OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();
                Java2DFrameConverter biConverter = new Java2DFrameConverter()) {
            if (mat == null || mat.empty()) {
                return null;
            }

            return biConverter.convert(matConverter.convert(mat));
        }
    }
}
