package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;

class AccountsApiDelegateTest {

    private final AccountsApiDelegate delegate = new AccountsApiDelegate() {};

    @Test
    void accountsPost_defaultImplementationReturnsEmptyMono() {
        Mono<ResponseEntity<com.bca.core_banking_service.dto.AccountResponse>> result = delegate.accountsPost(
                Mono.empty(), exchange());
        assertTrue(result.blockOptional().isEmpty());
    }

    @Test
    void accountsAccountIdDepositPost_defaultImplementationReturnsEmptyMono() {
        assertTrue(delegate.accountsAccountIdDepositPost("acc", Mono.empty(), exchange()).blockOptional().isEmpty());
    }

    @Test
    void accountsAccountIdWithdrawPost_defaultImplementationReturnsEmptyMono() {
        assertTrue(delegate.accountsAccountIdWithdrawPost("acc", Mono.empty(), exchange()).blockOptional().isEmpty());
    }

    @Test
    void accountsTransferPost_defaultImplementationReturnsEmptyMono() {
        assertTrue(delegate.accountsTransferPost(Mono.empty(), exchange()).blockOptional().isEmpty());
    }

    @Test
    void getRequestDefaultsToEmpty() {
        assertTrue(delegate.getRequest().isEmpty());
    }

    private MockServerWebExchange exchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.post("/accounts").build());
    }
}
