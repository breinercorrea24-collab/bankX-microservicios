package com.bca.wallets_service.infrastructure.output.persistence;

import com.bca.wallets_service.domain.model.Transaction;
import com.bca.wallets_service.domain.ports.output.TransactionRepository;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class TransactionRepositoryImpl implements TransactionRepository {

    private final Map<String, Transaction> transactions = new HashMap<>();

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
        return Mono.just(transaction);
    }
}