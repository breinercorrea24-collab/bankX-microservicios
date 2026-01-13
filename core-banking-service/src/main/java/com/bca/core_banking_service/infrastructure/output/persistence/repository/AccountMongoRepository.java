package com.bca.core_banking_service.infrastructure.output.persistence.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.product.account.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountMongoRepository extends ReactiveMongoRepository<Account, String> {
    Flux<Account> findByCustomerId(String customerId);
    Mono<Account> findByCustomerIdAndType(String customerId, AccountType type);
}