package com.bca.core_banking_service.domain.ports.output.persistence;

import com.bca.core_banking_service.infrastructure.input.dto.Credit;

import reactor.core.publisher.Mono;

public interface CreditRepository {
    Mono<Credit> save(Credit credit);
    Mono<Credit> findById(String id);
    Mono<Credit> findByCustomerId(String customerId);
    Mono<Long> countByCustomerId(String customerId);
}