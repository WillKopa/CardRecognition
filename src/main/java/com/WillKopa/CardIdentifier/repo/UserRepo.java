package com.WillKopa.CardIdentifier.repo;

import com.WillKopa.CardIdentifier.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entities.
 * <p>
 * Provides query methods for user operations including finding users by email
 * and fetching users with their card collections.
 * </p>
 */
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by email address with their card collection eagerly loaded.
     * <p>
     * Uses a JOIN FETCH to load the user's card list in a single query
     * to avoid N+1 query problems.
     * </p>
     *
     * @param email the email address to search for
     * @return an Optional containing the user with cards if found, or empty if not found
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cardList WHERE u.email = :email")
    Optional<User> findByEmailWithCards(@Param("email") String email);
}
