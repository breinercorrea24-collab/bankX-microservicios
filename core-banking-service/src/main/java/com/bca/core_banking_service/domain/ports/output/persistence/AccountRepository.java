package com.bca.core_banking_service.domain.ports.output.persistence;



import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.product.account.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository {
    Mono<Account> save(Account account);
    Mono<Account> findById(String id);
    Flux<Account> findByCustomerId(String customerId);
    Mono<Account> findByCustomerIdAndType(String customerId, AccountType type);
}