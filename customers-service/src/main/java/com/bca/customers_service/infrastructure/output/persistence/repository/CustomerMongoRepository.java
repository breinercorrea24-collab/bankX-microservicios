package com.bca.customers_service.infrastructure.output.persistence.repository;

import org.springframework.stereotype.Repository;

import com.bca.customers_service.infrastructure.output.persistence.entity.CustomerDocument;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerMongoRepository extends ReactiveMongoRepository<CustomerDocument, String> {

    Mono<Boolean> existsByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);
}