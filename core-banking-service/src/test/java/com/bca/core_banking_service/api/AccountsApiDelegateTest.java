package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AccountsApiDelegateTest {

    private final AccountsApiDelegate delegate = new AccountsApiDelegate() {};
    @Test
    void accountsPost_defaultImplementationFinishesEmpty() {
        MockServerWebExchange exchange = exchange();
        StepVerifier.create(delegate.accountsPost(Mono.empty(), exchange))
                .expectComplete()
                .verify();
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void accountsAccountIdDepositPost_defaultImplementationFinishesEmpty() {
        MockServerWebExchange exchange = exchange();
        StepVerifier.create(delegate.accountsAccountIdDepositPost("acc-1", Mono.empty(), exchange))
                .expectComplete()
                .verify();
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void accountsAccountIdWithdrawPost_defaultImplementationFinishesEmpty() {
        MockServerWebExchange exchange = exchange();
        StepVerifier.create(delegate.accountsAccountIdWithdrawPost("acc-1", Mono.empty(), exchange))
                .expectComplete()
                .verify();
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void accountsTransferPost_defaultImplementationFinishesEmpty() {
        MockServerWebExchange exchange = exchange();
        StepVerifier.create(delegate.accountsTransferPost(Mono.empty(), exchange))
                .expectComplete()
                .verify();
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void getRequestDefaultsToEmptyOptional() {
        assertTrue(delegate.getRequest().isEmpty());
    }

    private MockServerWebExchange exchange() {
        return MockServerWebExchange.from(
                MockServerHttpRequest.post("/accounts").accept(MediaType.APPLICATION_JSON).build());
    }
}
