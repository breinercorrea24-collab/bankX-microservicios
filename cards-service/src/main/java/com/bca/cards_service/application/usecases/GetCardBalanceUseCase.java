package com.bca.cards_service.application.usecases;


import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.ports.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class GetCardBalanceUseCase {

    private final CardRepository cardRepository;
    /* private final AccountService accountService; */

    public Mono<Card> execute(String cardId) {
        log.info("Executing GetCardBalanceUseCase for card: {}", cardId);
        return cardRepository.findById(cardId)
                .doOnSuccess(balance -> log.info("Successfully retrieved balance for card: {}", cardId))
                .doOnError(error -> log.error("Failed to get balance for card: {}", cardId, error));
    }


    public Mono<ResponseEntity<Boolean>> hasCards(String customerId) {
        log.info("Checking if customer {} has any cards", customerId);
        return cardRepository.findByCustomerId(customerId)
                .map(hasCard -> {
                    if (hasCard) {
                        log.info("Customer {} has cards", customerId);
                        return ResponseEntity.ok(true);
                    } else {
                        log.info("Customer {} does not have any cards", customerId);
                        return ResponseEntity.ok(false);
                    }
                });
    }
}