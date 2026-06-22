package com.WillKopa.CardIdentifier.repo;

import com.WillKopa.CardIdentifier.model.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Card entities.
 * <p>
 * Provides custom query methods for card operations including
 * updating card information and searching cards by name and number.
 * </p>
 */
@Repository
public interface CardRepo extends JpaRepository<Card, Integer> {
    /**
     * Updates card information in the database.
     * <p>
     * Updates the card set, set ID, and official printed total for a given card ID.
     * </p>
     *
     * @param updatedCard the card entity containing updated information
     */
    @Modifying
    @Transactional
    @Query("""
    UPDATE Card c
    SET c.cardSet = :#{#updatedCard.cardSet},
        c.cardSetId = :#{#updatedCard.cardSetId},
        c.setOfficialPrintedTotal = :#{#updatedCard.setOfficialPrintedTotal},
        c.imageUrlHigh = :#{#updatedCard.imageUrlHigh},
        c.imageUrlLow = :#{#updatedCard.imageUrlLow},
        c.marketPriceNormal = :#{#updatedCard.marketPriceNormal},
        c.marketPriceHolo = :#{#updatedCard.marketPriceHolo},
        c.marketPriceReverseHolo = :#{#updatedCard.marketPriceReverseHolo}
    WHERE c.id = :#{#updatedCard.id}
    """)
    void updateCard(@Param("updatedCard") Card updatedCard);

    /**
     * Finds a card by name and card number.
     * <p>
     * Performs a case-insensitive search for cards matching the given name pattern
     * and exact card number.
     * </p>
     *
     * @param name the card name pattern (supports SQL LIKE wildcards)
     * @param cardNumber the exact card number
     * @return the matching card, or null if not found
     */
    @Query(value = """
        SELECT c FROM Card c
        WHERE c.name LIKE :name AND c.cardNumber = :cardNumber AND c.setOfficialPrintedTotal = :setOfficialPrintedTotal
        """)
    List<Card> getCardsByNameAndNumberAndSetPrintedTotal(@Param("name") String name, @Param("cardNumber") String cardNumber, @Param("setOfficialPrintedTotal") Integer setOfficialPrintedTotal);

    @Query(value = """
        SELECT c FROM Card c
        WHERE c.name LIKE :name AND c.cardNumber = :cardNumber
        """)
    List<Card> getCardsByNameAndNumber(@Param("name") String name, @Param("cardNumber") String cardNumber);

    @Query(value = """
        SELECT c FROM Card c
        WHERE c.name LIKE :name
        """)
    List<Card> getCardsByName(@Param("name") String name);
}
