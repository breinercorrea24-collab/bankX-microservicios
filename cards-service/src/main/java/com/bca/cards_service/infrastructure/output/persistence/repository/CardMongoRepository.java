package com.bca.cards_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.cards_service.infrastructure.output.persistence.entity.CardDocument;

import reactor.core.publisher.Mono;

@Repository
public interface CardMongoRepository extends ReactiveMongoRepository<CardDocument, String> {
    Mono<CardDocument> findByCardId(String cardId);
}
