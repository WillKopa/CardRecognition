package com.WillKopa.CardIdentifier.model;

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
    private String cardNumber;
    private int setOfficialPrintedTotal;
}
