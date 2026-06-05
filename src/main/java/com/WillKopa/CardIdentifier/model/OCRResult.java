package com.WillKopa.CardIdentifier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OCRResult {
    private String name;
    private Integer cardNumber;
    private Integer setPrintedTotal;
}
