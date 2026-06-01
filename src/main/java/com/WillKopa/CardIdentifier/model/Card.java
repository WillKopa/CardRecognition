package com.WillKopa.CardIdentifier.model;

import com.WillKopa.CardIdentifier.converter.VectorConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String externalDbId;
    private String game;
    private String name;
    private String cardSet;
    private String cardSetId;
    private int cardNumber;
    private int setPrintedTotal;
    private String cardSetConcat;

    @Convert(converter = VectorConverter.class)
    @Column(name = "image_embedding", columnDefinition = "vector(1000)")
    private float[] imageEmbedding;

    @Column(columnDefinition = "jsonb")
    private String priceTypes;
    private Date lastUpdate;
}
