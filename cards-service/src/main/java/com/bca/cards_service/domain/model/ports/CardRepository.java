package com.bca.cards_service.domain.model.ports;

import com.bca.cards_service.domain.model.Card;
import com.bca.cards_service.domain.model.CardId;
import reactor.core.publisher.Mono;

public interface CardRepository {
    Mono<Card> save(Card card);
    Mono<Card> findById(CardId cardId);
}