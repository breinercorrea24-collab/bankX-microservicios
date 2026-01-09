package com.bca.core_banking_service.domain.ports.output;

import com.bca.core_banking_service.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionRepository {
    Mono<Transaction> save(Transaction transaction);
}