package com.WillKopa.CardIdentifier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity representing a trading card in the system.
 * <p>
 * Stores card information including external database references, game type,
 * name, set information, and card number.
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Card {
    /** The unique identifier for this card */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /** The external database ID for this card */
    private String externalDbId;
    /** The game system this card belongs to (e.g., Pokémon TCG) */
    private String game;
    /** The name of the card */
    private String name;
    /** The set name this card belongs to */
    private String cardSet;
    /** The set ID this card belongs to */
    private String cardSetId;
    /** The card number within its set */
    private String cardNumber;
    /** The official printed total for the set */
    private int setOfficialPrintedTotal;
}
