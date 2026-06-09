package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.exception.CardNotFoundException;
import com.WillKopa.CardIdentifier.model.Card;
import com.WillKopa.CardIdentifier.dto.request.CardCollectionRequest;
import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.model.UserCard;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import com.WillKopa.CardIdentifier.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public User getOrCreateUser(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String userName = jwt.getClaimAsString("name");

        return userRepo.findByEmail(email).orElseGet(() -> userRepo.save(
                User.builder()
                        .email(email)
                        .userName(userName)
                        .build()
        ));
    }

    @Transactional
    public User addCard(CardCollectionRequest addRequest, String email) throws CardNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")); // This should never be triggered
        try {
            user.setCollectionValue(user.getCollectionValue().add(new BigDecimal(addRequest.getMarketValue().toString())));
            user.getCardList().stream()
                    .filter(userCard -> userCard.getCard().getId().equals(addRequest.getCardId()))
                    .findFirst()
                    .ifPresentOrElse(
                            userCard -> userCard.setQuantity(userCard.getQuantity() + 1),
                            () -> {
                                    Card card = cardRepo.getReferenceById(addRequest.getCardId());
                                    user.getCardList().add(UserCard.builder()
                                    .user(user)
                                    .card(card)
                                    .quantity(1)
                                    .build());
                            }
                    );
            return user;
        } catch (EntityNotFoundException e) {
            throw new CardNotFoundException(String.format("Card with id: %s does not exist", addRequest.getCardId()));
        }
    }

    @Transactional
    public User removeCard(CardCollectionRequest removeRequest, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.getCardList().stream()
                .filter(userCard -> userCard.getCard().getId().equals(removeRequest.getCardId()))
                .findFirst()
                .ifPresent(
                        userCard -> {
                            user.setCollectionValue(user.getCollectionValue().subtract(new BigDecimal(removeRequest.getMarketValue().toString())));
                            if (userCard.getQuantity() > 1) {
                                userCard.setQuantity(userCard.getQuantity() - 1);
                            } else {
                                user.getCardList().remove(userCard);
                            }
                        }
                );

        return user;
    }
}
