package com.bca.core_banking_service.infrastructure.output.persistence.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.core_banking_service.infrastructure.output.persistence.entity.AccountEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountMongoRepository extends ReactiveMongoRepository<AccountEntity, String> {
    Flux<AccountEntity> findByCustomerId(String customerId);
    Mono<AccountEntity> findByCustomerIdAndType(String customerId, AccountEntity.AccountType type);
}