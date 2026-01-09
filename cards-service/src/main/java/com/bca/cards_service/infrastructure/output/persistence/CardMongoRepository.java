package com.bca.cards_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface CardMongoRepository extends ReactiveMongoRepository<CardDocument, String> {
    Mono<CardDocument> findByCardId(String cardId);
}
