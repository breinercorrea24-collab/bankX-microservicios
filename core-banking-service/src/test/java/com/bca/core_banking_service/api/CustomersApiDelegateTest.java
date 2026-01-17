package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CustomersApiDelegateTest {

    private final CustomersApiDelegate delegate = new CustomersApiDelegate() {};
    @Test
    void customersCustomerIdAccountsGet_defaultImplementationFinishesEmpty() {
        MockServerWebExchange exchange = exchange();
        StepVerifier.create(delegate.customersCustomerIdAccountsGet("cus-1", exchange))
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
                MockServerHttpRequest.get("/customers/cus-1/accounts").accept(MediaType.APPLICATION_JSON).build());
    }
}
