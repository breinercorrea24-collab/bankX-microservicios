package com.bca.core_banking_service.domain.ports.output;

import com.bca.core_banking_service.infrastructure.input.dto.Transaction;

import reactor.core.publisher.Mono;

public interface TransactionRepository {
    Mono<Transaction> save(Transaction transaction);
}