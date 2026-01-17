package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CustomersApiTest {

    private final List<String> invocations = new ArrayList<>();

    private final CustomersApi api = new CustomersApi() {
        private final CustomersApiDelegate delegate = new CustomersApiDelegate() {
            @Override
            public Mono<org.springframework.http.ResponseEntity<Flux<com.bca.core_banking_service.dto.AccountPolymorphicResponse>>> customersCustomerIdAccountsGet(
                    String customerId, org.springframework.web.server.ServerWebExchange exchange) {
                invocations.add("list:" + customerId);
                return Mono.empty();
            }
        };

        @Override
        public CustomersApiDelegate getDelegate() {
            return delegate;
        }
    };

    private final MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/customers/cus-1/accounts").build());

    @BeforeEach
    void clearInvocations() {
        invocations.clear();
    }

    @Test
    void customersCustomerIdAccountsGetRoutesToDelegate() {
        StepVerifier.create(api.customersCustomerIdAccountsGet("cus-1", exchange)).verifyComplete();
        assertEquals(List.of("list:cus-1"), invocations);
    }
}
