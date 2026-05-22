package com.WillKopa.CardIdentifier.model;

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

    @Column(name = "image_hash", columnDefinition = "BIT(64)")
    private String imageHash;

    @Column(name = "last_sold_price", columnDefinition = "BIGINT")
    private BigInteger lastSoldPrice;
    private Date lastUpdate;
}
