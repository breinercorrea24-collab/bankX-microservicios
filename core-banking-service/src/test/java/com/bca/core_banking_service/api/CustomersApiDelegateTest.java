package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;

class CustomersApiDelegateTest {

    private final CustomersApiDelegate delegate = new CustomersApiDelegate() {};

    @Test
    void defaultRequestReturnsEmptyOptional() {
        assertEquals(false, delegate.getRequest().isPresent());
    }

    @Test
    void customersCustomerIdAccountsGetSetsNotImplementedStatus() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/customers/cus-1/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .build());
        MockServerHttpResponse response = exchange.getResponse();

        Mono<?> result = delegate.customersCustomerIdAccountsGet("cus-1", exchange);

        assertNotNull(result);
        assertEquals(org.springframework.http.HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
    }
}
