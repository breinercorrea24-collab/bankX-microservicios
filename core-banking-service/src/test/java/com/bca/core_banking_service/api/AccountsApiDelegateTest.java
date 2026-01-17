package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.dto.AccountCreate;
import com.bca.core_banking_service.dto.TransferRequest;

import reactor.core.publisher.Mono;

class AccountsApiDelegateTest {

    private final AccountsApiDelegate delegate = new AccountsApiDelegate() {};

    @Test
    void handlesDepositRequestByReturningNotImplemented() {
        MockServerWebExchange exchange = webExchange();
        Mono<?> result = delegate.accountsAccountIdDepositPost("acc-1", Mono.empty(), exchange);

        assertNotNull(result);
        assertEquals(org.springframework.http.HttpStatus.NOT_IMPLEMENTED, exchange.getResponse().getStatusCode());
    }

    @Test
    void handlesWithdrawRequestByReturningNotImplemented() {
        MockServerWebExchange exchange = webExchange();
        Mono<?> result = delegate.accountsAccountIdWithdrawPost("acc-1", Mono.empty(), exchange);

        assertNotNull(result);
        assertEquals(org.springframework.http.HttpStatus.NOT_IMPLEMENTED, exchange.getResponse().getStatusCode());
    }

    @Test
    void handlesAccountCreationByReturningNotImplemented() {
        MockServerWebExchange exchange = webExchange();
        Mono<?> result = delegate.accountsPost(Mono.just(new AccountCreate()), exchange);

        assertNotNull(result);
        assertEquals(org.springframework.http.HttpStatus.NOT_IMPLEMENTED, exchange.getResponse().getStatusCode());
    }

    @Test
    void handlesTransferByReturningNotImplemented() {
        MockServerWebExchange exchange = webExchange();
        Mono<?> result = delegate.accountsTransferPost(Mono.just(new TransferRequest()), exchange);

        assertNotNull(result);
        assertEquals(org.springframework.http.HttpStatus.NOT_IMPLEMENTED, exchange.getResponse().getStatusCode());
    }

    private MockServerWebExchange webExchange() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return MockServerWebExchange.from(request);
    }
}
