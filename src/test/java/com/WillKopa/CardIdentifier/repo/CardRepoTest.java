package com.WillKopa.CardIdentifier.repo;

import com.WillKopa.CardIdentifier.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CardRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CardRepo cardRepo;

    private Card card1;
    private Card card2;
    private Card card3;

    @BeforeEach
    void setUp() {
        card1 = new Card();
        card1.setExternalDbId("base1-1");
        card1.setGame("Pokemon");
        card1.setName("Pikachu");
        card1.setCardSet("Base Set");
        card1.setCardSetId("base1");
        card1.setCardNumber("001");
        card1.setSetOfficialPrintedTotal(102);
        card1.setImageUrlLow("low1.jpg");
        card1.setImageUrlHigh("high1.jpg");
        card1.setMarketPriceNormal(10.5f);
        card1.setMarketPriceHolo(25.0f);
        card1.setMarketPriceReverseHolo(15.75f);

        card2 = new Card();
        card2.setExternalDbId("base1-4");
        card2.setGame("Pokemon");
        card2.setName("Charizard");
        card2.setCardSet("Base Set");
        card2.setCardSetId("base1");
        card2.setCardNumber("004");
        card2.setSetOfficialPrintedTotal(102);
        card2.setImageUrlLow("low2.jpg");
        card2.setImageUrlHigh("high2.jpg");
        card2.setMarketPriceNormal(100.0f);
        card2.setMarketPriceHolo(250.0f);
        card2.setMarketPriceReverseHolo(150.0f);

        card3 = new Card();
        card3.setExternalDbId("base2-1");
        card3.setGame("Pokemon");
        card3.setName("Pikachu");
        card3.setCardSet("Jungle");
        card3.setCardSetId("base2");
        card3.setCardNumber("001");
        card3.setSetOfficialPrintedTotal(64);
        card3.setImageUrlLow("low3.jpg");
        card3.setImageUrlHigh("high3.jpg");
        card3.setMarketPriceNormal(12.0f);
        card3.setMarketPriceHolo(30.0f);
        card3.setMarketPriceReverseHolo(18.0f);
    }

    @Test
    void testSaveCard() {
        Card savedCard = cardRepo.save(card1);

        assertNotNull(savedCard);
        assertNotNull(savedCard.getId());
        assertEquals("Pikachu", savedCard.getName());
        assertEquals("Base Set", savedCard.getCardSet());
    }

    @Test
    void testFindById() {
        Card savedCard = entityManager.persistFlushFind(card1);

        Card foundCard = cardRepo.findById(savedCard.getId()).orElse(null);

        assertNotNull(foundCard);
        assertEquals(savedCard.getId(), foundCard.getId());
        assertEquals("Pikachu", foundCard.getName());
    }

    @Test
    void testGetCardsByNameAndNumberAndSetPrintedTotal() {
        entityManager.persist(card1);
        entityManager.persist(card2);
        entityManager.flush();

        List<Card> results = cardRepo.getCardsByNameAndNumberAndSetPrintedTotal("%Pikachu%", "001", 102);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Pikachu", results.get(0).getName());
        assertEquals("001", results.get(0).getCardNumber());
        assertEquals(102, results.get(0).getSetOfficialPrintedTotal());
    }

    @Test
    void testGetCardsByNameAndNumberAndSetPrintedTotal_NoMatch() {
        entityManager.persist(card1);
        entityManager.persist(card2);
        entityManager.flush();

        List<Card> results = cardRepo.getCardsByNameAndNumberAndSetPrintedTotal("%Pikachu%", "001", 64);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetCardsByNameAndNumber() {
        entityManager.persist(card1);
        entityManager.persist(card3);
        entityManager.flush();

        List<Card> results = cardRepo.getCardsByNameAndNumber("%Pikachu%", "001");

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(card -> card.getName().contains("Pikachu")));
        assertTrue(results.stream().allMatch(card -> card.getCardNumber().equals("001")));
    }

    @Test
    void testGetCardsByNameAndNumber_NoMatch() {
        entityManager.persist(card1);
        entityManager.flush();

        List<Card> results = cardRepo.getCardsByNameAndNumber("%Charizard%", "001");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetCardsByName() {
        entityManager.persist(card1);
        entityManager.persist(card3);
        entityManager.flush();

        List<Card> results = cardRepo.getCardsByName("%Pikachu%");

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(card -> card.getName().contains("Pikachu")));
    }

    @Test
    void testGetCardsByName_NoMatch() {
        entityManager.persist(card1);
        entityManager.flush();

        List<Card> results = cardRepo.getCardsByName("%Bulbasaur%");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetCardsByName_CaseInsensitive() {
        entityManager.persist(card1);
        entityManager.flush();

        List<Card> results = cardRepo.getCardsByName("%pikachu%");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Pikachu", results.get(0).getName());
    }

    @Test
    void testUpdateCard() {
        Card savedCard = entityManager.persistFlushFind(card1);
        
        savedCard.setCardSet("Updated Set");
        savedCard.setCardSetId("updated1");
        savedCard.setSetOfficialPrintedTotal(200);
        savedCard.setImageUrlHigh("updated_high.jpg");
        savedCard.setImageUrlLow("updated_low.jpg");
        savedCard.setMarketPriceNormal(50.0f);
        savedCard.setMarketPriceHolo(100.0f);
        savedCard.setMarketPriceReverseHolo(75.0f);

        cardRepo.updateCard(savedCard);
        entityManager.flush();
        entityManager.clear();

        Card updatedCard = cardRepo.findById(savedCard.getId()).orElse(null);

        assertNotNull(updatedCard);
        assertEquals("Updated Set", updatedCard.getCardSet());
        assertEquals("updated1", updatedCard.getCardSetId());
        assertEquals(200, updatedCard.getSetOfficialPrintedTotal());
        assertEquals("updated_high.jpg", updatedCard.getImageUrlHigh());
        assertEquals("updated_low.jpg", updatedCard.getImageUrlLow());
        assertEquals(50.0f, updatedCard.getMarketPriceNormal());
        assertEquals(100.0f, updatedCard.getMarketPriceHolo());
        assertEquals(75.0f, updatedCard.getMarketPriceReverseHolo());
    }

    @Test
    void testDeleteCard() {
        Card savedCard = entityManager.persistFlushFind(card1);

        cardRepo.delete(savedCard);
        entityManager.flush();

        Card deletedCard = cardRepo.findById(savedCard.getId()).orElse(null);

        assertNull(deletedCard);
    }

    @Test
    void testCount() {
        entityManager.persist(card1);
        entityManager.persist(card2);
        entityManager.persist(card3);
        entityManager.flush();

        long count = cardRepo.count();

        assertEquals(3, count);
    }

    @Test
    void testExistsById() {
        Card savedCard = entityManager.persistFlushFind(card1);

        assertTrue(cardRepo.existsById(savedCard.getId()));
        assertFalse(cardRepo.existsById(999));
    }
}
