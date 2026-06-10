package com.WillKopa.CardIdentifier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user in the system.
 * <p>
 * Stores user information including email, username, collection value,
 * and the user's card collection.
 * </p>
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_users")
public class User {
    /** The unique identifier for this user */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /** The user's email address */
    private String email;
    /** The user's display name */
    private String userName;

    /** The total value of the user's card collection */
    @Builder.Default
    private BigDecimal collectionValue = new BigDecimal(0);

    /** The list of cards in the user's collection */
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCard> cardList = new ArrayList<>();
}
