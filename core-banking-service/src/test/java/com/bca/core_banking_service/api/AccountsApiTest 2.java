package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.dto.AccountCreate;
import com.bca.core_banking_service.dto.AmountRequest;
import com.bca.core_banking_service.dto.TransferRequest;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AccountsApiTest {

    private final List<String> invocations = new ArrayList<>();

    private final AccountsApi api = new AccountsApi() {
        private final AccountsApiDelegate delegate = new AccountsApiDelegate() {
            @Override
            public reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.bca.core_banking_service.dto.TransactionResponse>> accountsAccountIdDepositPost(
                    String accountId, Mono<AmountRequest> amountRequest, org.springframework.web.server.ServerWebExchange exchange) {
                invocations.add("deposit:" + accountId);
                return Mono.empty();
            }

            @Override
            public reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.bca.core_banking_service.dto.TransactionResponse>> accountsAccountIdWithdrawPost(
                    String accountId, Mono<AmountRequest> amountRequest, org.springframework.web.server.ServerWebExchange exchange) {
                invocations.add("withdraw:" + accountId);
                return Mono.empty();
            }

            @Override
            public reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.bca.core_banking_service.dto.AccountResponse>> accountsPost(
                    Mono<AccountCreate> accountCreate, org.springframework.web.server.ServerWebExchange exchange) {
                invocations.add("post");
                return Mono.empty();
            }

            @Override
            public reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.bca.core_banking_service.dto.TransactionResponse>> accountsTransferPost(
                    Mono<TransferRequest> transferRequest, org.springframework.web.server.ServerWebExchange exchange) {
                invocations.add("transfer");
                return Mono.empty();
            }
        };

        @Override
        public AccountsApiDelegate getDelegate() {
            return delegate;
        }
    };

    private final MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/accounts").build());

    @BeforeEach
    void clearInvocations() {
        invocations.clear();
    }

    @Test
    void accountsPostRoutesToDelegate() {
        StepVerifier.create(api.accountsPost(Mono.empty(), exchange)).verifyComplete();
        assertEquals(List.of("post"), invocations);
    }

    @Test
    void accountsAccountIdDepositPostRoutesToDelegate() {
        StepVerifier.create(api.accountsAccountIdDepositPost("acc-1", Mono.empty(), exchange)).verifyComplete();
        assertEquals(List.of("deposit:acc-1"), invocations);
    }

    @Test
    void accountsAccountIdWithdrawPostRoutesToDelegate() {
        StepVerifier.create(api.accountsAccountIdWithdrawPost("acc-2", Mono.empty(), exchange)).verifyComplete();
        assertEquals(List.of("withdraw:acc-2"), invocations);
    }

    @Test
    void accountsTransferPostRoutesToDelegate() {
        StepVerifier.create(api.accountsTransferPost(Mono.empty(), exchange)).verifyComplete();
        assertEquals(List.of("transfer"), invocations);
    }
}
