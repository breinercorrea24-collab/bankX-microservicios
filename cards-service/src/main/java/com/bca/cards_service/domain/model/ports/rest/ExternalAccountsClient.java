package com.bca.cards_service.domain.model.ports.rest;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;

import com.bca.cards_service.infrastructure.output.rest.dto.AccountBalanceResponse;

import reactor.core.publisher.Mono;

public interface ExternalAccountsClient {
    Mono<ResponseEntity<AccountBalanceResponse>>  AccountWithdrawal(String accountId, BigDecimal amount);
    Mono<ResponseEntity<AccountBalanceResponse>>  AccountBalance(String accountId);
}
