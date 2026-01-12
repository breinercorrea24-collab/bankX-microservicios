package com.bca.core_banking_service.infrastructure.output.persistence;

import com.bca.core_banking_service.domain.model.Transaction;
import com.bca.core_banking_service.domain.ports.output.TransactionRepository;
import com.bca.core_banking_service.infrastructure.output.persistence.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.TransactionMongoRepository;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionMongoRepository mongoRepository;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return mongoRepository.save(TransactionMapper.toEntity(transaction))
                .map(TransactionMapper::toDomain);
    }
}