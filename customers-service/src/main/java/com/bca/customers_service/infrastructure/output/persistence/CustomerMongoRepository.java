package com.bca.customers_service.infrastructure.output.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerMongoRepository extends ReactiveMongoRepository<CustomerDocument, String> {

    Mono<Boolean> existsByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);
}