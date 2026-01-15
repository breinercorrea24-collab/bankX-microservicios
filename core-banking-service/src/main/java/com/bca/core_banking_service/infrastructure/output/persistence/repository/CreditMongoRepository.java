package com.bca.core_banking_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.core_banking_service.infrastructure.output.persistence.entity.CreditEntity;

import reactor.core.publisher.Mono;

@Repository
public interface CreditMongoRepository extends ReactiveMongoRepository<CreditEntity, String> {
    Mono<CreditEntity> findByCustomerId(String customerId);
    Mono<Long> countByCustomerId(String customerId);
}