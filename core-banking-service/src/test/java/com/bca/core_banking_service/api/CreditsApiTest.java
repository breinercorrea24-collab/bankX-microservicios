package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.dto.AmountRequest;
import com.bca.core_banking_service.dto.CreditCreate;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CreditsApiTest {

    private final List<String> invocations = new ArrayList<>();

    private final CreditsApi api = new CreditsApi() {
        private final CreditsApiDelegate delegate = new CreditsApiDelegate() {
            @Override
            public reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.bca.core_banking_service.dto.CreditResponse>> creditsPost(
                    Mono<CreditCreate> creditCreate, org.springframework.web.server.ServerWebExchange exchange) {
                invocations.add("create");
                return Mono.empty();
            }

            @Override
            public reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.bca.core_banking_service.dto.CreditPaymentResponse>> creditsCreditIdPaymentPost(
                    String creditId, Mono<AmountRequest> amountRequest, org.springframework.web.server.ServerWebExchange exchange) {
                invocations.add("payment:" + creditId);
                return Mono.empty();
            }
        };

        @Override
        public CreditsApiDelegate getDelegate() {
            return delegate;
        }
    };

    private final MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/credits").build());

    @BeforeEach
    void clearInvocations() {
        invocations.clear();
    }

    @Test
    void creditsPostRoutesToDelegate() {
        StepVerifier.create(api.creditsPost(Mono.empty(), exchange)).verifyComplete();
        assertEquals(List.of("create"), invocations);
    }

    @Test
    void creditsCreditIdPaymentPostRoutesToDelegate() {
        StepVerifier.create(api.creditsCreditIdPaymentPost("credit-1", Mono.empty(), exchange)).verifyComplete();
        assertEquals(List.of("payment:credit-1"), invocations);
    }
}
