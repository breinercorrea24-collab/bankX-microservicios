package com.bca.core_banking_service.infrastructure.output.persistence;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.AccountMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<Account> findByCustomerIdAndType(String customerId, AccountType type) {
        return mongoRepository.findByCustomerIdAndType(customerId, type);
    }
}