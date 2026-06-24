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

    private static final int TEST_CARD_ID_1 = 1;
    private static final int TEST_CARD_ID_2 = 2;
    private static final int TEST_SET_PRINTED_TOTAL = 102;
    private static final int TEST_SET_PRINTED_TOTAL_MISMATCH = 100;
    private static final float TEST_LOW_PRICE_1 = 10.5f;
    private static final float TEST_MARKET_PRICE_1 = 25.0f;
    private static final float TEST_MID_PRICE_1 = 15.75f;
    private static final float TEST_LOW_PRICE_2 = 12.0f;
    private static final float TEST_MARKET_PRICE_2 = 30.0f;
    private static final float TEST_MID_PRICE_2 = 20.0f;
    private static final int EXPECTED_SINGLE_RESULT = 1;
    private static final int EXPECTED_DOUBLE_RESULT = 2;
    private static final int FIRST_RESULT_INDEX = 0;
    private static final String TEST_CARD_NAME = "Pikachu";
    private static final String TEST_CARD_NUMBER = "001";
    private static final String TEST_SET_PRINTED_TOTAL_STR = "102";
    private static final String TEST_EXTERNAL_DB_ID_1 = "base1-1";
    private static final String TEST_EXTERNAL_DB_ID_2 = "base1-2";
    private static final String TEST_CARD_SET = "Base Set";
    private static final String TEST_LOW_IMAGE_URL_1 = "low.jpg";
    private static final String TEST_HIGH_IMAGE_URL_1 = "high.jpg";
    private static final String TEST_LOW_IMAGE_URL_2 = "low2.jpg";
    private static final String TEST_HIGH_IMAGE_URL_2 = "high2.jpg";
    private static final String INVALID_IMAGE_MESSAGE = "Invalid image";
    private static final String NO_OCR_RESULT_MESSAGE = "No OCR result";

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
        ocrResult = new OCRResult(TEST_CARD_NAME, TEST_CARD_NUMBER, TEST_SET_PRINTED_TOTAL_STR);
        card = new Card();
        card.setId(TEST_CARD_ID_1);
        card.setName(TEST_CARD_NAME);
        card.setCardNumber(TEST_CARD_NUMBER);
        card.setSetOfficialPrintedTotal(TEST_SET_PRINTED_TOTAL);
        card.setExternalDbId(TEST_EXTERNAL_DB_ID_1);
        card.setCardSet(TEST_CARD_SET);
        
        cardSearchResult = new CardSearchResult(
            TEST_CARD_ID_1,
            TEST_CARD_NAME,
            TEST_CARD_NUMBER,
            TEST_SET_PRINTED_TOTAL,
            TEST_EXTERNAL_DB_ID_1,
            TEST_CARD_SET,
            TEST_LOW_PRICE_1,
            TEST_MARKET_PRICE_1,
            TEST_MID_PRICE_1,
            TEST_LOW_IMAGE_URL_1,
            TEST_HIGH_IMAGE_URL_1
        );
    }

    @Test
    void testIdentifyCard_Success() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL)))
            .thenReturn(List.of(card));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(EXPECTED_SINGLE_RESULT, result.size());
        assertEquals(TEST_CARD_NAME, result.get(FIRST_RESULT_INDEX).getName());
        
        verify(ocrService).performPokemonOcr(imageFile);
        verify(cardRepo).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL));
        verify(tcgDexService).setCardPriceAndImageURL(card);
        verify(cardConverter).toCardSearchResult(card);
    }

    @Test
    void testIdentifyCard_FallbackToNameAndNumber() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL)))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByNameAndNumber(anyString(), eq(TEST_CARD_NUMBER)))
            .thenReturn(List.of(card));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(EXPECTED_SINGLE_RESULT, result.size());
        
        verify(cardRepo).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL));
        verify(cardRepo).getCardsByNameAndNumber(anyString(), eq(TEST_CARD_NUMBER));
        verify(tcgDexService).setCardPriceAndImageURL(card);
    }

    @Test
    void testIdentifyCard_FallbackToNameOnly() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL)))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByNameAndNumber(anyString(), eq(TEST_CARD_NUMBER)))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByName(anyString()))
            .thenReturn(List.of(card));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(EXPECTED_SINGLE_RESULT, result.size());
        
        verify(cardRepo).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL));
        verify(cardRepo).getCardsByNameAndNumber(anyString(), eq(TEST_CARD_NUMBER));
        verify(cardRepo).getCardsByName(anyString());
        verify(tcgDexService).setCardPriceAndImageURL(card);
    }

    @Test
    void testIdentifyCard_CardNeedsUpdate() throws InvalidImageException, NoOcrResultException {
        card.setCardSet(null);
        
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL)))
            .thenReturn(List.of(card));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(EXPECTED_SINGLE_RESULT, result.size());
        
        verify(tcgDexService).setCardPriceAndImageURL(card);
        verify(tcgDexService).updateCard(card);
    }

    @Test
    void testIdentifyCard_CardUpdatedButSetMismatch() throws InvalidImageException, NoOcrResultException {
        card.setCardSet(null);
        card.setSetOfficialPrintedTotal(TEST_SET_PRINTED_TOTAL_MISMATCH);
        
        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL)))
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
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL)))
            .thenReturn(new ArrayList<>());
        when(cardRepo.getCardsByNameAndNumber(anyString(), eq(TEST_CARD_NUMBER)))
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
            .thenThrow(new InvalidImageException(INVALID_IMAGE_MESSAGE));

        assertThrows(InvalidImageException.class, () -> cardService.identifyCard(imageFile));
        
        verify(ocrService).performPokemonOcr(imageFile);
        verify(cardRepo, never()).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), anyString(), anyInt());
    }

    @Test
    void testIdentifyCard_ThrowsNoOcrResultException() throws InvalidImageException, NoOcrResultException {
        when(ocrService.performPokemonOcr(imageFile))
            .thenThrow(new NoOcrResultException(NO_OCR_RESULT_MESSAGE));

        assertThrows(NoOcrResultException.class, () -> cardService.identifyCard(imageFile));
        
        verify(ocrService).performPokemonOcr(imageFile);
        verify(cardRepo, never()).getCardsByNameAndNumberAndSetPrintedTotal(anyString(), anyString(), anyInt());
    }

    @Test
    void testIdentifyCard_MultipleResults() throws InvalidImageException, NoOcrResultException {
        Card card2 = new Card();
        card2.setId(TEST_CARD_ID_2);
        card2.setName(TEST_CARD_NAME);
        card2.setCardNumber(TEST_CARD_NUMBER);
        card2.setSetOfficialPrintedTotal(TEST_SET_PRINTED_TOTAL);
        card2.setExternalDbId(TEST_EXTERNAL_DB_ID_2);
        card2.setCardSet(TEST_CARD_SET);
        
        CardSearchResult cardSearchResult2 = new CardSearchResult(
            TEST_CARD_ID_2,
            TEST_CARD_NAME,
            TEST_CARD_NUMBER,
            TEST_SET_PRINTED_TOTAL,
            TEST_EXTERNAL_DB_ID_2,
            TEST_CARD_SET,
            TEST_LOW_PRICE_2,
            TEST_MARKET_PRICE_2,
            TEST_MID_PRICE_2,
            TEST_LOW_IMAGE_URL_2,
            TEST_HIGH_IMAGE_URL_2
        );

        when(ocrService.performPokemonOcr(imageFile)).thenReturn(ocrResult);
        when(cardRepo.getCardsByNameAndNumberAndSetPrintedTotal(anyString(), eq(TEST_CARD_NUMBER), eq(TEST_SET_PRINTED_TOTAL)))
            .thenReturn(List.of(card, card2));
        when(cardConverter.toCardSearchResult(card)).thenReturn(cardSearchResult);
        when(cardConverter.toCardSearchResult(card2)).thenReturn(cardSearchResult2);

        List<CardSearchResult> result = cardService.identifyCard(imageFile);

        assertNotNull(result);
        assertEquals(EXPECTED_DOUBLE_RESULT, result.size());
        
        verify(tcgDexService, times(EXPECTED_DOUBLE_RESULT)).setCardPriceAndImageURL(any(Card.class));
    }
}
