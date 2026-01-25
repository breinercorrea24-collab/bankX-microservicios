package com.bca.customers_service.application;

import com.bca.customers_service.domain.dto.AccountSummary;
import com.bca.customers_service.domain.dto.CreditSummary;
import com.bca.customers_service.domain.dto.CustomerSummaryResponse;
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
    void shouldReturnSummaryWhenServicesSucceed() {
        accountService.setAccounts(Flux.just(
                new AccountSummary("a1", AccountSummary.AccountType.SAVINGS, "USD", 100.0, 90.0, AccountSummary.AccountStatus.ACTIVE),
                new AccountSummary("a2", AccountSummary.AccountType.CURRENT, "PEN", 200.0, 180.0, AccountSummary.AccountStatus.BLOCKED)
        ));
        creditService.setCredits(Flux.just(
                new CreditSummary("c1", 500.0, 250.0, "USD", CreditSummary.CreditStatus.ACTIVE)
        ));

        StepVerifier.create(useCase.execute("cust-1"))
                .assertNext(response -> {
                    assertThat(response.getCustomerId()).isEqualTo("cust-1");
                    assertThat(response.getTotalAccounts()).isEqualTo(2);
                    assertThat(response.getTotalBalance()).isEqualTo(300.0);
                    assertThat(response.getAccounts()).hasSize(2);
                    assertThat(response.getAccounts().get(0).getAccountId()).isEqualTo("a1");
                    assertThat(response.getCredits()).hasSize(1);
                    assertThat(response.getCredits().get(0).getCreditId()).isEqualTo("c1");
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateAccountServiceError() {
        accountService.setAccounts(Flux.error(new RuntimeException("accounts error")));
        creditService.setCredits(Flux.just(
                new CreditSummary("c2", 100.0, 50.0, "PEN", CreditSummary.CreditStatus.CANCELLED)
        ));

        StepVerifier.create(useCase.execute("cust-err"))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("accounts error"))
                .verify();
    }

    @Test
    void shouldPropagateCreditServiceError() {
        accountService.setAccounts(Flux.just(
                new AccountSummary("a3", AccountSummary.AccountType.SAVINGS, "USD", 50.0, 50.0, AccountSummary.AccountStatus.ACTIVE)
        ));
        creditService.setCredits(Flux.error(new RuntimeException("credits failure")));

        StepVerifier.create(useCase.execute("cust-credit-err"))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("credits failure"))
                .verify();
    }

    private static class StubAccountService implements AccountService {
        private Flux<AccountSummary> accounts = Flux.empty();

        void setAccounts(Flux<AccountSummary> accounts) {
            this.accounts = accounts;
        }

        @Override
        public Flux<AccountSummary> getAccountsByCustomerId(String customerId) {
            return accounts;
        }
    }

    private static class StubCreditService implements CreditService {
        private Flux<CreditSummary> credits = Flux.empty();

        void setCredits(Flux<CreditSummary> credits) {
            this.credits = credits;
        }

        @Override
        public Flux<CreditSummary> getCreditsByCustomerId(String customerId) {
            return credits;
        }
    }
}
