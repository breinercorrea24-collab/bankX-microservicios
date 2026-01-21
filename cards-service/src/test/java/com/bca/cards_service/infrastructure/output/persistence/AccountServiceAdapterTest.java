package com.bca.cards_service.infrastructure.output.persistence;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class AccountServiceAdapterTest {

    private final AccountServiceAdapter adapter = new AccountServiceAdapter();

    @Test
    void returnsFixedBalance() {
        StepVerifier.create(adapter.getAccountBalance("acc-123"))
                .expectNext(BigDecimal.valueOf(1700.00))
                .verifyComplete();
    }

    @Test
    void emitsNoErrors() {
        StepVerifier.create(adapter.getAccountBalance("any"))
                .expectNextCount(1)
                .verifyComplete();
    }
}
