package com.bca.core_banking_service.infrastructure.input.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.application.ports.input.usecases.AccountUseCase;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.dto.AccountCreate;
import com.bca.core_banking_service.dto.AccountResponse;
import com.bca.core_banking_service.dto.AmountRequest;
import com.bca.core_banking_service.dto.TransactionResponse;
import com.bca.core_banking_service.dto.TransferRequest;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AccountApiDelegateImplTest {

    @Mock
    private AccountUseCase accountUseCase;

    private AccountApiDelegateImpl delegate;

    @BeforeEach
    void setUp() {
        delegate = new AccountApiDelegateImpl(accountUseCase);
    }

    @Test
    void accountsPost_returnsCreatedAccountResponse() {
        SavingsAccount account = sampleAccount("acc-1", BigDecimal.valueOf(100));
        AccountCreate createRequest = new AccountCreate()
                .customerId("customer-1")
                .currency("USD")
                .type(AccountCreate.TypeEnum.SAVINGS);

        when(accountUseCase.createAccount("customer-1", AccountType.SAVINGS, "USD"))
                .thenReturn(Mono.just(account));

        StepVerifier.create(delegate.accountsPost(Mono.just(createRequest), mockExchange("/accounts")))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    AccountResponse body = response.getBody();
                    assertNotNull(body);
                    assertEquals("customer-1", body.getCustomerId());
                    assertEquals(com.bca.core_banking_service.dto.AccountType.SAVINGS, body.getType());
                })
                .verifyComplete();
    }

    @Test
    void accountsAccountIdDepositPost_returnsTransactionResponse() {
        SavingsAccount account = sampleAccount("acc-1", BigDecimal.valueOf(150));
        AmountRequest request = new AmountRequest().amount(50F);

        when(accountUseCase.deposit("acc-1", BigDecimal.valueOf(50.0)))
                .thenReturn(Mono.just(account));

        StepVerifier.create(delegate.accountsAccountIdDepositPost(
                "acc-1",
                Mono.just(request),
                mockExchange("/accounts/acc-1/deposit")))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    TransactionResponse body = response.getBody();
                    assertNotNull(body);
                    assertEquals("acc-1", body.getAccountId());
                    assertEquals(TransactionResponse.TypeEnum.DEPOSIT, body.getType());
                })
                .verifyComplete();
    }

    @Test
    void accountsAccountIdWithdrawPost_returnsTransactionResponse() {
        SavingsAccount account = sampleAccount("acc-1", BigDecimal.valueOf(70));
        AmountRequest request = new AmountRequest().amount(30F);

        when(accountUseCase.withdraw("acc-1", BigDecimal.valueOf(30.0)))
                .thenReturn(Mono.just(account));

        StepVerifier.create(delegate.accountsAccountIdWithdrawPost(
                "acc-1",
                Mono.just(request),
                mockExchange("/accounts/acc-1/withdraw")))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    TransactionResponse body = response.getBody();
                    assertNotNull(body);
                    assertEquals(TransactionResponse.TypeEnum.WITHDRAW, body.getType());
                })
                .verifyComplete();
    }

    @Test
    void accountsTransferPost_returnsTransactionResponse() {
        SavingsAccount account = sampleAccount("acc-1", BigDecimal.valueOf(40));
        TransferRequest transferRequest = new TransferRequest()
                .fromId("acc-1")
                .toId("acc-2")
                .amount(20F);

        when(accountUseCase.transfer("acc-1", "acc-2", BigDecimal.valueOf(20.0)))
                .thenReturn(Mono.just(account));

        StepVerifier.create(delegate.accountsTransferPost(
                Mono.just(transferRequest),
                mockExchange("/accounts/transfer")))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    TransactionResponse body = response.getBody();
                    assertNotNull(body);
                    assertEquals(TransactionResponse.TypeEnum.TRANSFER, body.getType());
                })
                .verifyComplete();
    }

    private SavingsAccount sampleAccount(String id, BigDecimal balance) {
        SavingsAccount account = new SavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ZERO,
                balance);
        account.setId(id);
        account.setType(AccountType.SAVINGS);
        return account;
    }

    private MockServerWebExchange mockExchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.post(path).build());
    }
}
