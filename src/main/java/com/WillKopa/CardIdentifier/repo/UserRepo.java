package com.WillKopa.CardIdentifier.repo;

import com.WillKopa.CardIdentifier.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cardList WHERE u.email = :email")
    Optional<User> findByEmailWithCards(@Param("email") String email);
}
