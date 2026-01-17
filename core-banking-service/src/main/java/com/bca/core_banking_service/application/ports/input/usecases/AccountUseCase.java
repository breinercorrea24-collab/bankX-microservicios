package com.bca.core_banking_service.application.ports.input.usecases;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.product.account.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountUseCase {
    Mono<Account> createAccount(String customerId, CustomerType customerType, AccountType type, String currency);
    Flux<Account> getAccountsByCustomer(String customerId);
    Mono<Account> deposit(String accountId, java.math.BigDecimal amount);
    Mono<Account> withdraw(String accountId, java.math.BigDecimal amount);
    Mono<Account> transfer(String fromAccountId, String toAccountId, java.math.BigDecimal amount);
}