package com.bca.core_banking_service.infrastructure.output.persistence;

import com.bca.core_banking_service.domain.model.Account;
import com.bca.core_banking_service.domain.ports.output.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface AccountMongoRepository extends ReactiveMongoRepository<Account, String> {
    Flux<Account> findByCustomerId(String customerId);
    Mono<Account> findByCustomerIdAndType(String customerId, Account.AccountType type);
}

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMongoRepository mongoRepository;

    @Override
    public Mono<Account> save(Account account) {
        return mongoRepository.save(account);
    }

    @Override
    public Mono<Account> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public Flux<Account> findByCustomerId(String customerId) {
        return mongoRepository.findByCustomerId(customerId);
    }

    @Override
    public Mono<Account> findByCustomerIdAndType(String customerId, Account.AccountType type) {
        return mongoRepository.findByCustomerIdAndType(customerId, type);
    }
}