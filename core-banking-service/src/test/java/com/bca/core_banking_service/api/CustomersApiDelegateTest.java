package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CustomersApiDelegateTest {

    private final CustomersApiDelegate delegate = new CustomersApiDelegate() {};
    private final MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/customers/cus-1/accounts").build());

    @Test
    void customersCustomerIdAccountsGet_defaultImplementationFinishesEmpty() {
        StepVerifier.create(delegate.customersCustomerIdAccountsGet("cus-1", exchange))
                .expectComplete()
                .verify();
    }

    @Test
    void getRequestDefaultsToEmptyOptional() {
        assertTrue(delegate.getRequest().isEmpty());
    }
}
