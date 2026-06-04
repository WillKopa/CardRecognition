package com.WillKopa.CardIdentifier.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class OCRResult {
    private List<String> results;
}
