package com.WillKopa.CardIdentifier.repo;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardCondition;
import com.WillKopa.CardIdentifier.model.CardVariation;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.model.UserCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepo userRepo;

    private User user1;
    private User user2;
    private Card card;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
            .email("user1@example.com")
            .userName("user1")
            .collectionValue(new BigDecimal("100.00"))
            .cardList(new ArrayList<>())
            .build();

        user2 = User.builder()
            .email("user2@example.com")
            .userName("user2")
            .collectionValue(new BigDecimal("50.00"))
            .cardList(new ArrayList<>())
            .build();

        card = new Card();
        card.setExternalDbId("base1-1");
        card.setGame("Pokemon");
        card.setName("Pikachu");
        card.setCardSet("Base Set");
        card.setCardSetId("base1");
        card.setCardNumber("001");
        card.setSetOfficialPrintedTotal(102);
        card.setImageUrlLow("low.jpg");
        card.setImageUrlHigh("high.jpg");
        card.setMarketPriceNormal(10.5f);
        card.setMarketPriceHolo(25.0f);
        card.setMarketPriceReverseHolo(15.75f);
    }

    @Test
    void testSaveUser() {
        User savedUser = userRepo.save(user1);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("user1@example.com", savedUser.getEmail());
        assertEquals("user1", savedUser.getUserName());
    }

    @Test
    void testFindById() {
        User savedUser = entityManager.persistFlushFind(user1);

        User foundUser = userRepo.findById(savedUser.getId()).orElse(null);

        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals("user1@example.com", foundUser.getEmail());
    }

    @Test
    void testFindByEmail() {
        entityManager.persist(user1);
        entityManager.flush();

        Optional<User> foundUser = userRepo.findByEmail("user1@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("user1@example.com", foundUser.get().getEmail());
        assertEquals("user1", foundUser.get().getUserName());
    }

    @Test
    void testFindByEmail_NotFound() {
        entityManager.persist(user1);
        entityManager.flush();

        Optional<User> foundUser = userRepo.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByEmailWithCards() {
        Card savedCard = entityManager.persistFlushFind(card);
        
        UserCard userCard = UserCard.builder()
            .card(savedCard)
            .quantity(2)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
        
        user1.getCardList().add(userCard);
        User savedUser = entityManager.persistFlushFind(user1);

        Optional<User> foundUser = userRepo.findByEmailWithCards("user1@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("user1@example.com", foundUser.get().getEmail());
        assertNotNull(foundUser.get().getCardList());
        assertEquals(1, foundUser.get().getCardList().size());
    }

    @Test
    void testFindByEmailWithCards_EmptyList() {
        entityManager.persistFlushFind(user1);

        Optional<User> foundUser = userRepo.findByEmailWithCards("user1@example.com");

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getCardList());
        assertTrue(foundUser.get().getCardList().isEmpty());
    }

    @Test
    void testFindByEmailWithCards_NotFound() {
        entityManager.persist(user1);
        entityManager.flush();

        Optional<User> foundUser = userRepo.findByEmailWithCards("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testDeleteByEmail() {
        entityManager.persist(user1);
        entityManager.flush();

        userRepo.deleteByEmail("user1@example.com");
        entityManager.flush();

        Optional<User> deletedUser = userRepo.findByEmail("user1@example.com");

        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testDeleteByEmail_UserNotFound() {
        userRepo.deleteByEmail("nonexistent@example.com");
        entityManager.flush();

        // Should not throw an exception
        assertTrue(true);
    }

    @Test
    void testUserWithMultipleCards() {
        Card savedCard = entityManager.persistFlushFind(card);
        
        UserCard userCard1 = UserCard.builder()
            .card(savedCard)
            .quantity(2)
            .cardCondition(CardCondition.NEAR_MINT)
            .cardVariation(CardVariation.NORMAL)
            .build();
        
        UserCard userCard2 = UserCard.builder()
            .card(savedCard)
            .quantity(1)
            .cardCondition(CardCondition.LIGHTLY_PLAYED)
            .cardVariation(CardVariation.HOLOGRAPHIC)
            .build();
        
        user1.getCardList().add(userCard1);
        user1.getCardList().add(userCard2);
        User savedUser = entityManager.persistFlushFind(user1);

        Optional<User> foundUser = userRepo.findByEmailWithCards("user1@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(2, foundUser.get().getCardList().size());
    }

    @Test
    void testCount() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        long count = userRepo.count();

        assertEquals(2, count);
    }

    @Test
    void testExistsById() {
        User savedUser = entityManager.persistFlushFind(user1);

        assertTrue(userRepo.existsById(savedUser.getId()));
        assertFalse(userRepo.existsById(999));
    }

    @Test
    void testDeleteUser() {
        User savedUser = entityManager.persistFlushFind(user1);

        userRepo.delete(savedUser);
        entityManager.flush();

        User deletedUser = userRepo.findById(savedUser.getId()).orElse(null);

        assertNull(deletedUser);
    }

    @Test
    void testUpdateUser() {
        User savedUser = entityManager.persistFlushFind(user1);
        
        savedUser.setUserName("updatedUser");
        savedUser.setCollectionValue(new BigDecimal("200.00"));

        User updatedUser = userRepo.save(savedUser);
        entityManager.flush();
        entityManager.clear();

        User foundUser = userRepo.findById(updatedUser.getId()).orElse(null);

        assertNotNull(foundUser);
        assertEquals("updatedUser", foundUser.getUserName());
        assertEquals(new BigDecimal("200.00"), foundUser.getCollectionValue());
    }

    @Test
    void testFindAll() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        List<User> users = userRepo.findAll();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testUniqueEmailConstraint() {
        entityManager.persist(user1);
        entityManager.flush();

        User duplicateUser = User.builder()
            .email("user1@example.com")
            .userName("differentUser")
            .collectionValue(new BigDecimal("0.00"))
            .cardList(new ArrayList<>())
            .build();

        assertThrows(Exception.class, () -> {
            entityManager.persist(duplicateUser);
            entityManager.flush();
        });
    }
}
