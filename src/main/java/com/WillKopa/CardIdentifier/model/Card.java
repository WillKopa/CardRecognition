package com.WillKopa.CardIdentifier.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Card {
    @Id
    private int id;
    private String game;
    private String name;
    private String cardSet;
    private BigInteger imageHash;
    private BigInteger lastSoldPrice;
    private Date lastUpdate;
}
