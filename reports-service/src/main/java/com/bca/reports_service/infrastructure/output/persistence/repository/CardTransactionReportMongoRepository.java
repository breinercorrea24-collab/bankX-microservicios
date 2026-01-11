package com.bca.reports_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CardTransactionReportMongoRepository extends ReactiveMongoRepository<CardTransactionReportDocument, String> {
    Mono<CardTransactionReportDocument> findByCardId(String cardId);
    Flux<CardTransactionReportDocument> findTop10ByCardIdOrderByTransactionDateDesc(String cardId);

}
