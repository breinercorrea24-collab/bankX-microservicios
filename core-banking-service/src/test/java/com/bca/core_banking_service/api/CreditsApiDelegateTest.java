package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.dto.CreditCreate;

import reactor.core.publisher.Mono;

class CreditsApiDelegateTest {

    private final CreditsApiDelegate delegate = new CreditsApiDelegate() {};

    @Test
    void getRequestReturnsEmptyOptional() {
        assertEquals(false, delegate.getRequest().isPresent());
    }

    @Test
    void creditsPostReturnsNotImplemented() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/credits")
                .accept(MediaType.APPLICATION_JSON)
                .build());

        Mono<?> result = delegate.creditsPost(Mono.just(new CreditCreate()), exchange);

        assertNotNull(result);
        assertEquals(org.springframework.http.HttpStatus.NOT_IMPLEMENTED, exchange.getResponse().getStatusCode());
    }
}
