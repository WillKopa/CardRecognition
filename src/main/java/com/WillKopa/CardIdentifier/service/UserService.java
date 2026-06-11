package com.WillKopa.CardIdentifier.service;

import com.WillKopa.CardIdentifier.exception.CardNotFoundException;
import com.WillKopa.CardIdentifier.exception.UserAlreadyExistsException;
import com.WillKopa.CardIdentifier.exception.UserNotFoundException;
import com.WillKopa.CardIdentifier.model.*;
import com.WillKopa.CardIdentifier.dto.request.CardCollectionRequest;
import com.WillKopa.CardIdentifier.repo.CardRepo;
import com.WillKopa.CardIdentifier.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for managing user accounts and card collections.
 * <p>
 * Provides methods for user creation, adding cards to collections,
 * removing cards from collections, and calculating collection values.
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    UserRepo userRepo;
    CardRepo cardRepo;

    /**
     * Retrieves an existing user from JWT token information.
     * <p>
     * Looks up a user by email from the JWT token.
     * </p>
     *
     * @param jwt the JWT token containing user information
     * @return the existing user
     */
    public User getUser(Jwt jwt) throws UserNotFoundException {
        String email = jwt.getClaimAsString("email");
        String userName = jwt.getClaimAsString("name");

        return userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }
    /**
     * Creates a new user.
     * <p>
     * Uses information from the JWT token to create a new user.
     * If the user already exists, throws a UserAlreadyExistsException.
     * </p>
     *
     * @param jwt the JWT token containing user information
     * @return the newly created user
     * @throws UserAlreadyExistsException if the user already exists
     */
    @Transactional
    public User createUser(Jwt jwt) throws UserAlreadyExistsException{

        String email = jwt.getClaimAsString("email");
        String userName = jwt.getClaimAsString("name");

        if (userRepo.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        return userRepo.save(User.builder()
                .email(email)
                .userName(userName)
                .build());
    }

    /**
     * Adds a card to the user's collection.
     * <p>
     * If the card already exists in the user's collection, increments the quantity.
     * Otherwise, adds a new card with quantity 1. Updates the collection value
     * with the card's market value.
     * </p>
     *
     * @param addRequest the request containing card ID and market value
     * @param email the user's email address
     * @return the updated user with the new card added
     * @throws CardNotFoundException if the card with the given ID doesn't exist
     */
    @Transactional
    public User addCard(CardCollectionRequest addRequest, String email) throws CardNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")); // This should never be triggered
        try {
            user.setCollectionValue(user.getCollectionValue().add(new BigDecimal(addRequest.getMarketValue().toString())));
            user.getCardList().stream()
                    .filter(userCard -> userCard.getCard().getId().equals(addRequest.getCardId())
                            && userCard.getCardCondition().equals(addRequest.getCardCondition())
                            && userCard.getCardVariation().equals(addRequest.getCardVariation())
                    )
                    .findFirst()
                    .ifPresentOrElse(
                            userCard -> userCard.setQuantity(userCard.getQuantity() + 1),
                            () -> {
                                    Card card = cardRepo.getReferenceById(addRequest.getCardId());
                                    user.getCardList().add(UserCard.builder()
                                    .user(user)
                                    .card(card)
                                    .quantity(1)
                                    .cardCondition(addRequest.getCardCondition())
                                    .cardVariation(addRequest.getCardVariation())
                                    .build());
                            }
                    );
            return user;
        } catch (EntityNotFoundException e) {
            throw new CardNotFoundException(String.format("Card with id: %s does not exist", addRequest.getCardId()));
        }
    }

    /**
     * Overloaded method to add a card to the user's collection.
     * <p>
     * Convenience method that creates a CardCollectionRequest from the provided
     * parameters and delegates to the main addCard method.
     * </p>
     *
     * @param id the card ID
     * @param marketPrice the market price of the card
     * @param email the user's email address
     * @return the updated user with the new card added
     */
    @Transactional
    public User addCard(Integer id, Float marketPrice, CardCondition cardCondition, CardVariation cardVariation, String email) {
        CardCollectionRequest request = CardCollectionRequest.builder()
                .cardId(id)
                .marketValue(marketPrice)
                .cardCondition(cardCondition)
                .cardVariation(cardVariation)
                .build();
        return addCard(request, email);
    }

    /**
     * Removes a card from the user's collection.
     * <p>
     * Decrements the card quantity if more than one copy exists.
     * Removes the card entirely if only one copy exists.
     * Updates the collection value by subtracting the card's market value.
     * </p>
     *
     * @param removeRequest the request containing card ID and market value
     * @param email the user's email address
     * @return the updated user with the card removed
     */
    @Transactional
    public User removeCard(CardCollectionRequest removeRequest, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.getCardList().stream()
                .filter(userCard -> userCard.getCard().getId().equals(removeRequest.getCardId())
                        && userCard.getCardCondition().equals(removeRequest.getCardCondition())
                        && userCard.getCardVariation().equals(removeRequest.getCardVariation())
                )
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

    @Transactional
    public void deleteUser(String email) {
        userRepo.deleteByEmail(email);
    }
}
