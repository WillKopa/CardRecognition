package com.WillKopa.CardIdentifier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    /** The card number within its set
     * It might make sense to store this as an int, but the external database often ensures it is a 3 digit number i.e 001
     * */
    private String cardNumber;
    /** The official printed total for the set
     * The total number of cards does not seem to share the same issue the card number does so it can be stored as an int
     * */
    private int setOfficialPrintedTotal;
    /** url to low res image **/
    private String imageUrlLow;
    /** url to high res image **/
    private String imageUrlHigh;
    /** Normal cardVariation market price **/
    private Float marketPriceNormal;
    /** Holo cardVariation market price **/
    private Float marketPriceHolo;
    /** Reverse Holo cardVariation market price **/
    private Float marketPriceReverseHolo;
}
