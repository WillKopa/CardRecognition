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
    INSERT INTO card (game, name, card_set, image_embedding, last_sold_price, last_update)
    VALUES (:game, :name, :cardSet, CAST(:embedding AS vector), :lastSoldPrice, :lastUpdate)
    """, nativeQuery = true)

    void saveWithEmbedding(
            @Param("game") String game,
            @Param("name") String name,
            @Param("cardSet") String cardSet,
            @Param("embedding") String embedding,
            @Param("lastSoldPrice") BigInteger lastSoldPrice,
            @Param("lastUpdate") Date lastUpdate
    );
}
