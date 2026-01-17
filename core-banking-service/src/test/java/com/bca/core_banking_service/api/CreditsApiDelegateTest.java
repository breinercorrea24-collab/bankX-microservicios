package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CreditsApiDelegateTest {

    private final CreditsApiDelegate delegate = new CreditsApiDelegate() {};
    @Test
    void creditsPost_defaultImplementationFinishesEmpty() {
        MockServerWebExchange exchange = exchange();
        StepVerifier.create(delegate.creditsPost(Mono.empty(), exchange))
                .expectComplete()
                .verify();
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void creditsCreditIdPaymentPost_defaultImplementationFinishesEmpty() {
        MockServerWebExchange exchange = exchange();
        StepVerifier.create(delegate.creditsCreditIdPaymentPost("cred-1", Mono.empty(), exchange))
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
                MockServerHttpRequest.post("/credits").accept(MediaType.APPLICATION_JSON).build());
    }
}
