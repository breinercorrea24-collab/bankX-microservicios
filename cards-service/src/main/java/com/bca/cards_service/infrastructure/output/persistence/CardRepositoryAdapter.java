package com.bca.cards_service.infrastructure.output.persistence;

import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.domain.model.ports.CardRepository;
import com.bca.cards_service.infrastructure.output.persistence.repository.CardMongoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CardRepositoryAdapter implements CardRepository {

    private final CardMongoRepository cardMongoRepository;

    @Override
    public Mono<Card> save(Card card) {
        log.debug("Saving card: {}", card);
        return cardMongoRepository.save(card)
                .doOnSuccess(savedCard -> log.info("Card saved successfully: {}", savedCard.getId()))
                .doOnError(error -> log.error("Failed to save card: {}", card.getId(), error));
    }

    @Override
    public Mono<Card> findById(String cardId) {
        log.debug("Finding card by ID: {}", cardId);
        return cardMongoRepository.findById(cardId)
                .doOnSuccess(card -> log.debug("Card found: {}", cardId))
                .doOnError(error -> log.error("Failed to find card: {}", cardId, error))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Card not found: {}", cardId);
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Boolean> findByCustomerId(String customerId) {
        log.debug("Finding card by Customer ID: {}", customerId);
        return cardMongoRepository.findByCustomerId(customerId)
                .map(cardDocument -> {
                    log.debug("Card found for Customer ID: {}", customerId);
                    return true;
                })
                .defaultIfEmpty(false)
                .doOnError(error -> log.error("Failed to find card for Customer ID: {}", customerId, error));
    }

}
