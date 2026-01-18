package com.bca.cards_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.cards_service.domain.model.card.Card;

import reactor.core.publisher.Mono;

@Repository
public interface CardMongoRepository extends ReactiveMongoRepository<Card, String> {
    Mono<Card> findById(String cardId);

    Mono<Card> findByCustomerId(String customerId);
}
