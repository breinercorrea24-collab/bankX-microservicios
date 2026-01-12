package com.bca.core_banking_service.domain.ports.output;

import com.bca.core_banking_service.infrastructure.input.dto.Credit;

import reactor.core.publisher.Mono;

public interface CreditRepository {
    Mono<Credit> save(Credit credit);
    Mono<Credit> findById(String id);
}