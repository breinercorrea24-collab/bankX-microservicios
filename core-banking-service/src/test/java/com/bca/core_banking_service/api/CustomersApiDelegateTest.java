package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class CustomersApiDelegateTest {

    private final CustomersApiDelegate delegate = new CustomersApiDelegate() {};

    @Test
    void customersCustomerIdAccountsGet_defaultImplementationReturnsEmptyMono() {
        Mono<org.springframework.http.ResponseEntity<Flux<com.bca.core_banking_service.dto.AccountPolymorphicResponse>>> result = delegate.customersCustomerIdAccountsGet(
                "customer", exchange());
        assertTrue(result.blockOptional().isEmpty());
    }

    @Test
    void getRequestDefaultsToEmpty() {
        assertTrue(delegate.getRequest().isEmpty());
    }

    private MockServerWebExchange exchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/customers/cus/accounts").build());
    }
}
