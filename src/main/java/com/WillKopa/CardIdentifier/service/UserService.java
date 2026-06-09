package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.model.CardAddRequest;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import com.WillKopa.CardIdentifier.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    UserRepo userRepo;
    CardRepo cardRepo;

    @Transactional
    public void addCard(CardAddRequest addRequest, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Card card = cardRepo.getReferenceById(addRequest.getCardId());
            user.setCollectionValue(user.getCollectionValue().add(new BigDecimal(addRequest.getMarketValue().toString())));
            user.getCardList().add(card);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(String.format("Card with id: %s does not exist", addRequest.getCardId()));
        }
    }
}
