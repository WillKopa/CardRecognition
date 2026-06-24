package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.converter.CardConverter;
import com.WillKopa.CardIdentifier.dto.response.CardSearchResult;
import com.WillKopa.CardIdentifier.exception.InvalidImageException;
import com.WillKopa.CardIdentifier.exception.NoOcrResultException;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.OCRResult;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepo cardRepo;

    @Mock
    private OcrService ocrService;

    @Mock
    private TCGDexService tcgDexService;

    @Mock
    private CardConverter cardConverter;

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private CardService cardService;

    private OCRResult ocrResult;
    private Card card;
    private CardSearchResult cardSearchResult;

    @BeforeEach
    void setUp() {
        ocrResult = new OCRResult("Pikachu", "001", "102");
        card = new Card();
        card.setId(1);
        card.setName("Pikachu");
        card.setCardNumber("001");
        card.setSetOfficialPrintedTotal(102);
        card.setExternalDbId("base1-1");
        card.setCardSet("Base Set");
        
        cardSearchResult = new CardSearchResult(
            1,
            "Pikachu",
            "001",
            102,
            "base1-1",
            "Base Set",
            10.5f,
            25.0f,
            15.75f,
            "low.jpg",
            "high.jpg"
        );
    }

    @Test
    void testIdentifyCard_Success() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102)))
            .thenReturn(List.of(card));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pikachu", result.get(0).getName());
        
        verify(ocrService).performPokemonOcr(imageFile);
        verify(cardRepo).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102));
        verify(tcgDexService).setCardPriceAndImageURL(card);
        verify(cardConverter).toCardSearchResult(card);
    }

    @Test
    void testIdentifyCard_FallbackToNameAndNumber() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102)))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByNameAndNumber(anyString(), eq("001")))
            .thenReturn(List.of(card));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(cardRepo).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102));
        verify(cardRepo).getCardsByNameAndNumber(anyString(), eq("001"));
        verify(tcgDexService).setCardPriceAndImageURL(card);
    }

    @Test
    void testIdentifyCard_FallbackToNameOnly() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102)))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByNameAndNumber(anyString(), eq("001")))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByName(anyString()))
            .thenReturn(List.of(card));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(cardRepo).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102));
        verify(cardRepo).getCardsByNameAndNumber(anyString(), eq("001"));
        verify(cardRepo).getCardsByName(anyString());
        verify(tcgDexService).setCardPriceAndImageURL(card);
    }

    @Test
    void testIdentifyCard_CardNeedsUpdate() throws InvalidImageException, NoOcrResultException {
        card.setCardSet(null);
        
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102)))
            .thenReturn(List.of(card));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(tcgDexService).setCardPriceAndImageURL(card);
        verify(tcgDexService).updateCard(card);
    }

    @Test
    void testIdentifyCard_CardUpdatedButSetMismatch() throws InvalidImageException, NoOcrResultException {
        card.setCardSet(null);
        card.setSetOfficialPrintedTotal(100);
        
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102)))
            .thenReturn(List.of(card));

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(tcgDexService).setCardPriceAndImageURL(card);
        verify(tcgDexService).updateCard(card);
        verify(cardConverter, never()).toCardSearchResult(card);
    }

    @Test
    void testIdentifyCard_NoResultsFound() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102)))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByNameAndNumber(anyString(), eq("001")))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByName(anyString()))
            .thenReturn(new ArrayList<>());

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testIdentifyCard_ThrowsInvalidImageException() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile))
            .thenThrow(new InvalidImageException("Invalid image"));

        assertThrows(InvalidImageException.class, () -> cardService.identifyCard(imageFile));
        
        verify(ocrService).performPokemonOcr(imageFile);
        verify(cardRepo, never()).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), anyString(), anyInt());
    }

    @Test
    void testIdentifyCard_ThrowsNoOcrResultException() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile))
            .thenThrow(new NoOcrResultException("No OCR result"));

        assertThrows(NoOcrResultException.class, () -> cardService.identifyCard(imageFile));
        
        verify(ocrService).performPokemonOcr(imageFile);
        verify(cardRepo, never()).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), anyString(), anyInt());
    }

    @Test
    void testIdentifyCard_MultipleResults() throws InvalidImageException, NoOcrResultException {
        Card card2 = new Card();
        card2.setId(2);
        card2.setName("Pikachu");
        card2.setCardNumber("001");
        card2.setSetOfficialPrintedTotal(102);
        card2.setExternalDbId("base1-2");
        card2.setCardSet("Base Set");
        
        CardSearchResult cardSearchResult2 = new CardSearchResult(
            2,
            "Pikachu",
            "001",
            102,
            "base1-2",
            "Base Set",
            12.0f,
            30.0f,
            20.0f,
            "low2.jpg",
            "high2.jpg"
        );

        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq("001"), eq(102)))
            .thenReturn(List.of(card, card2));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);
        when(cardConverter.toCardSearchResult(card2)).thenReturn(cardSearchResult2);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(tcgDexService, times(2)).setCardPriceAndImageURL(any(Card.class));
    }
}
