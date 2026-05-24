package com.WillKopa.CardIdentifier.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class EmbeddingService {
    private final OrtEnvironment env;
    private final OrtSession session;

    public EmbeddingService() throws OrtException {
        env = OrtEnvironment.getEnvironment();
        String modelPath = getClass().getClassLoader()
                .getResource("models/mobilenetv2-7.onnx")
                .getPath();
        session = env.createSession(modelPath, new OrtSession.SessionOptions());
    }


    public float[] imageToEmbeddings(MultipartFile imageFile) throws IOException, OrtException {
        BufferedImage image = ImageIO.read(imageFile.getInputStream());
        return bufferedImageToEmbeddings(image);
    }

    public float[] bufferedImageToEmbeddings(BufferedImage image) throws OrtException {
        float[][][][] input = preprocess(image);
        OnnxTensor tensor = OnnxTensor.createTensor(env, input);
        try (OrtSession.Result result = session.run(Map.of("data", tensor))) {
            float[][] output = (float[][]) result.get(0).getValue();
            return output[0];
        }
    }

    private float[][][][] preprocess(BufferedImage original) {
        BufferedImage resized = new BufferedImage(224, 224, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(original.getScaledInstance(224, 224, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();

        float[][][][] tensor = new float[1][3][224][224];
        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int rgb = resized.getRGB(x, y);
                tensor[0][0][y][x] = (((rgb >> 16) & 0xFF) / 127.5f) - 1f;
                tensor[0][1][y][x] = (((rgb >> 8)  & 0xFF) / 127.5f) - 1f;
                tensor[0][2][y][x] = ((rgb & 0xFF)          / 127.5f) - 1f;
            }
        }
        return tensor;
    }
}
