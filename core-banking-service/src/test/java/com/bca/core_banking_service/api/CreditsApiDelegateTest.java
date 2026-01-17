package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;

class CreditsApiDelegateTest {

    private final CreditsApiDelegate delegate = new CreditsApiDelegate() {};

    @Test
    void creditsPost_defaultImplementationReturnsEmptyMono() {
        Mono<ResponseEntity<com.bca.core_banking_service.dto.CreditResponse>> result = delegate.creditsPost(
                Mono.empty(), exchange());
        assertTrue(result.blockOptional().isEmpty());
    }

    @Test
    void creditsCreditIdPaymentPost_defaultImplementationReturnsEmptyMono() {
        assertTrue(delegate.creditsCreditIdPaymentPost("credit", Mono.empty(), exchange()).blockOptional().isEmpty());
    }

    @Test
    void getRequestDefaultsToEmpty() {
        assertTrue(delegate.getRequest().isEmpty());
    }

    private MockServerWebExchange exchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.post("/credits").build());
    }
}
