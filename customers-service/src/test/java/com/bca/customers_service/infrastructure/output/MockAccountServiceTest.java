package com.bca.customers_service.infrastructure.output;

import com.bca.customers_service.domain.dto.AccountSummary;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MockAccountServiceTest {

    private final MockAccountService service = new MockAccountService();

    @Test
    void getAccountsByCustomerId_returnsFixedMockData() {
        StepVerifier.create(service.getAccountsByCustomerId("cust-123").collectList())
                .assertNext(accounts -> {
                    assertThat(accounts).hasSize(2);
                    assertThat(accounts.get(0).getAccountId()).isEqualTo("acc-001");
                    assertThat(accounts.get(0).getAccountType()).isEqualTo(AccountSummary.AccountType.SAVINGS);
                    assertThat(accounts.get(0).getCurrency()).isEqualTo("PEN");
                    assertThat(accounts.get(1).getAccountId()).isEqualTo("acc-002");
                    assertThat(accounts.get(1).getCurrency()).isEqualTo("USD");
                })
                .verifyComplete();
    }
}
