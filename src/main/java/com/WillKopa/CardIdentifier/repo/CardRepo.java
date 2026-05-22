package com.WillKopa.CardIdentifier.repo;

import com.WillKopa.CardIdentifier.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CardRepo extends JpaRepository<Card, Integer> {
    @Query(value = "SELECT * FROM Card WHERE " +
            "bit_count(image_hash # CAST(:cardHash AS bigint)) <= :threshold " +
            "ORDER BY bit_count(image_hash # CAST(:cardHash AS bigint)) ASC " +
            "LIMIT 1", nativeQuery = true)
    Card identifyCard(@Param("cardHash") BigInteger cardHash, @Param("threshold") int threshold);
}
