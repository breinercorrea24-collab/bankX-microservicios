package com.bca.cards_service.domain.model.ports;

import com.bca.cards_service.domain.model.card.Card;
import reactor.core.publisher.Mono;

public interface CardRepository {
    Mono<Card> save(Card card);
    Mono<Card> findById(String cardId);
    Mono<Boolean> findByCustomerId(String customerId);
}