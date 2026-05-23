package com.WillKopa.CardIdentifier.model;

import com.WillKopa.CardIdentifier.converter.VectorConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String game;
    private String name;
    private String cardSet;

    @Convert(converter = VectorConverter.class)
    @Column(name = "image_embedding", columnDefinition = "vector(1000)")
    private float[] imageEmbedding;

    @Column(name = "last_sold_price", columnDefinition = "BIGINT")
    private BigInteger lastSoldPrice;
    private Date lastUpdate;
}
