package com.bca.core_banking_service.infrastructure.output.persistence;

import com.bca.core_banking_service.domain.model.Transaction;
import com.bca.core_banking_service.domain.ports.output.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

interface TransactionMongoRepository extends ReactiveMongoRepository<Transaction, String> {
}

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionMongoRepository mongoRepository;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return mongoRepository.save(transaction);
    }
}