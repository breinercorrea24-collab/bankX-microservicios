package com.bca.customers_service.infrastructure.output;

import com.bca.customers_service.domain.dto.CreditSummary;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class MockCreditServiceTest {

    private final MockCreditService service = new MockCreditService();

    @Test
    void getCreditsByCustomerId_returnsFixedMockData() {
        StepVerifier.create(service.getCreditsByCustomerId("cust-123").collectList())
                .assertNext(credits -> {
                    assertThat(credits).hasSize(1);
                    CreditSummary credit = credits.get(0);
                    assertThat(credit.getCreditId()).isEqualTo("cred-01");
                    assertThat(credit.getTotalAmount()).isEqualTo(30000.00);
                    assertThat(credit.getPendingDebt()).isEqualTo(15000.00);
                    assertThat(credit.getCurrency()).isEqualTo("PEN");
                    assertThat(credit.getStatus()).isEqualTo(CreditSummary.CreditStatus.ACTIVE);
                })
                .verifyComplete();
    }
}
