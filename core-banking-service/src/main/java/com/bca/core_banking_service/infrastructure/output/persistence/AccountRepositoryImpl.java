package com.bca.core_banking_service.infrastructure.output.persistence;


import com.bca.core_banking_service.domain.ports.output.AccountRepository;
import com.bca.core_banking_service.infrastructure.input.dto.Account;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.AccountEntity;
import com.bca.core_banking_service.infrastructure.output.persistence.mapper.AccountMapper;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.AccountMongoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMongoRepository mongoRepository;

    @Override
    public Mono<Account> save(Account account) {
        return mongoRepository.save(AccountMapper.toEntity(account))
                .map(AccountMapper::toDomain);
    }

    @Override
    public Mono<Account> findById(String id) {
        return mongoRepository.findById(id)
                .map(AccountMapper::toDomain);
    }

    @Override
    public Flux<Account> findByCustomerId(String customerId) {
        return mongoRepository.findByCustomerId(customerId)
                .map(AccountMapper::toDomain);
    }

    @Override
    public Mono<Account> findByCustomerIdAndType(String customerId, Account.AccountType type) {
        return mongoRepository.findByCustomerIdAndType(customerId, AccountEntity.AccountType.valueOf(type.name()))
                .map(AccountMapper::toDomain);
    }
}