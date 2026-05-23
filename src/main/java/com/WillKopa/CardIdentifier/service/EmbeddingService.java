package com.WillKopa.CardIdentifier.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

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

    public float[] embed(BufferedImage image) throws OrtException {
        System.out.println("Beginning embed");
        float[][][][] input = preprocess(image);
        OnnxTensor tensor = OnnxTensor.createTensor(env, input);
        System.out.println("Begin OnnxTensor");
        try (OrtSession.Result result = session.run(Map.of("data", tensor))) {
            float[][] output = (float[][]) result.get(0).getValue();
            System.out.println("In onnxTensor");
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
