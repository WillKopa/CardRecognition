package com.WillKopa.CardIdentifier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a card in a user's collection.
 * <p>
 * This is a join table entity that links users to cards with quantity information.
 * Tracks how many copies of each card a user owns.
 * </p>
 */
// New entity for the join table
@Entity
@Table(name = "user_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCard {

    /** The unique identifier for this user-card relationship */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** The user who owns this card */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** The card in the user's collection */
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    /** The quantity of this card owned by the user */
    private Integer quantity;
}