package com.bca.core_banking_service.application.ports.input.usecases;

import com.bca.core_banking_service.infrastructure.input.dto.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountUseCase {
    Mono<Account> createAccount(String customerId, Account.AccountType type, String currency);
    Flux<Account> getAccountsByCustomer(String customerId);
    Mono<Account> deposit(String accountId, java.math.BigDecimal amount);
    Mono<Account> withdraw(String accountId, java.math.BigDecimal amount);
    Mono<Account> transfer(String fromAccountId, String toAccountId, java.math.BigDecimal amount);
}