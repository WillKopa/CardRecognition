package com.WillKopa.CardIdentifier.repo;

import com.WillKopa.CardIdentifier.model.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;

@Repository
public interface CardRepo extends JpaRepository<Card, Integer> {
    @Query(value = """
        SELECT * FROM card
        WHERE image_embedding <-> CAST(:embedding AS vector) < :threshold 
        ORDER BY image_embedding <-> CAST(:embedding AS vector)
        LIMIT 1
        """, nativeQuery = true)
    Card identifyCard(@Param("embedding") String embedding, @Param("threshold") float threshold);

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO card ("external_db_id", game, name, card_set, card_set_id, card_number, set_printed_total, card_set_concat, image_embedding, price_types, last_update)
    VALUES (:externalDbId, :game, :name, :cardSet, :cardSetId, :cardNumber, :setPrintedTotal, :cardSetConcat, CAST(:embedding AS vector), CAST(:priceTypes AS jsonb), :lastUpdate)
    """, nativeQuery = true)

    void saveWithEmbedding(
            @Param("externalDbId") String externalDbId,
            @Param("game") String game,
            @Param("name") String name,
            @Param("cardSet") String cardSet,
            @Param("cardSetId") String cardSetId,
            @Param("cardNumber") int cardNumber,
            @Param("setPrintedTotal") int setPrintedTotal,
            @Param("cardSetConcat") String cardSetConcat,
            @Param("embedding") String embedding,
            @Param("priceTypes") String priceTypes,
            @Param("lastUpdate") Date lastUpdate
    );
}
