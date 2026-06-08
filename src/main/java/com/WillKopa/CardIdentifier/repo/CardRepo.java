package com.WillKopa.CardIdentifier.repo;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardSearchResult;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CardRepo extends JpaRepository<Card, Integer> {
    @Modifying
    @Transactional
    @Query("""
    UPDATE Card c
    SET c.cardSet = :#{#updatedCard.cardSet},
        c.cardSetId = :#{#updatedCard.cardSetId},
        c.setOfficialPrintedTotal = :#{updatedCard.setOfficialPrintedTotal}
    WHERE c.id = :#{#updatedCard.id}
    """)
    void updateCard(@Param("updatedCard") Card updatedCard);

    @Query(value = """
        SELECT name, card_set, card_number, external_db_id FROM card
        WHERE name LIKE :name AND card_number = :cardNumber
        """, nativeQuery = true)
    CardSearchResult getCardsByNameAndNumber(@Param("name") String name, @Param("cardNumber") String cardNumber);
}
