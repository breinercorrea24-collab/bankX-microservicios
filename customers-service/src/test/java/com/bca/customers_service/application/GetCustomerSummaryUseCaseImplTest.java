package com.bca.customers_service.application;

import com.bca.customers_service.domain.dto.AccountSummary;
import com.bca.customers_service.domain.dto.CreditSummary;
import com.bca.customers_service.domain.ports.AccountService;
import com.bca.customers_service.domain.ports.CreditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetCustomerSummaryUseCaseImplTest {

    private GetCustomerSummaryUseCaseImpl useCase;
    private StubAccountService accountService;
    private StubCreditService creditService;

    @BeforeEach
    void setUp() {
        accountService = new StubAccountService();
        creditService = new StubCreditService();
        useCase = new GetCustomerSummaryUseCaseImpl(accountService, creditService);
    }

    @Test
    void shouldAggregateAccountsAndCredits() {
        accountService.accounts = List.of(
                new AccountSummary("a1", AccountSummary.AccountType.SAVINGS, "PEN", 1500.0, 1400.0, AccountSummary.AccountStatus.ACTIVE),
                new AccountSummary("a2", AccountSummary.AccountType.CURRENT, "USD", 500.0, 500.0, AccountSummary.AccountStatus.ACTIVE)
        );

        creditService.credits = List.of(
                new CreditSummary("c1", 1000.0, 250.0, "USD", CreditSummary.CreditStatus.ACTIVE)
        );

        StepVerifier.create(useCase.execute("cust-1"))
                .assertNext(response -> {
                    assertThat(response.getCustomerId()).isEqualTo("cust-1");
                    assertThat(response.getTotalAccounts()).isEqualTo(2);
                    assertThat(response.getTotalBalance()).isEqualTo(2000.0);
                    assertThat(response.getAccounts()).containsExactlyElementsOf(accountService.accounts);
                    assertThat(response.getCredits()).containsExactlyElementsOf(creditService.credits);
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorsFromDependencies() {
        accountService.error = new RuntimeException("accounts error");

        StepVerifier.create(useCase.execute("cust-err"))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("accounts error"))
                .verify();
    }

    private static class StubAccountService implements AccountService {
        List<AccountSummary> accounts = List.of();
        RuntimeException error;

        @Override
        public Flux<AccountSummary> getAccountsByCustomerId(String customerId) {
            if (error != null) {
                return Flux.error(error);
            }
            return Flux.fromIterable(accounts);
        }
    }

    private static class StubCreditService implements CreditService {
        List<CreditSummary> credits = List.of();

        @Override
        public Flux<CreditSummary> getCreditsByCustomerId(String customerId) {
            return Flux.fromIterable(credits);
        }
    }
}
