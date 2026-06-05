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
    @Query(value = """
    INSERT INTO card ("external_db_id", game, name, card_set, card_set_id, card_number, set_printed_total, price_types, last_update)
    VALUES (:externalDbId, :game, :name, :cardSet, :cardSetId, :cardNumber, :setPrintedTotal, CAST(:priceTypes AS jsonb), :lastUpdate)
    """, nativeQuery = true)

    void saveWithEmbedding(
            @Param("externalDbId") String externalDbId,
            @Param("game") String game,
            @Param("name") String name,
            @Param("cardSet") String cardSet,
            @Param("cardSetId") String cardSetId,
            @Param("cardNumber") int cardNumber,
            @Param("setPrintedTotal") int setPrintedTotal,
            @Param("priceTypes") String priceTypes,
            @Param("lastUpdate") Date lastUpdate
    );

    @Query(value = """
        SELECT name, card_set, card_number, external_db_id FROM card
        WHERE name LIKE :name AND card_number = :cardNumber AND set_printed_total = :setPrintedTotal
        LIMIT 1
        """, nativeQuery = true)
    CardSearchResult getCardsByNameAndCardSetConcat(@Param("name") String name, @Param("cardNumber") int cardNumber, @Param("setPrintedTotal") int setPrintedTotal);
}
