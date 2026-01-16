package com.bca.core_banking_service.infrastructure.input.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.application.ports.input.usecases.AccountUseCase;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.dto.AccountPolymorphicResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CustomerApiDelegateImplTest {

    @Mock
    private AccountUseCase accountUseCase;

    private CustomerApiDelegateImpl delegate;

    @BeforeEach
    void setUp() {
        delegate = new CustomerApiDelegateImpl(accountUseCase);
    }

    @Test
    void customersCustomerIdAccountsGet_returnsFluxOfPolymorphicResponses() {
        SavingsAccount account = sampleSavingsAccount();

        when(accountUseCase.getAccountsByCustomer("customer-1"))
                .thenReturn(Flux.just(account));

        Mono<org.springframework.http.ResponseEntity<Flux<AccountPolymorphicResponse>>> responseMono =
                delegate.customersCustomerIdAccountsGet("customer-1", mockExchange("/customers/customer-1/accounts"));

        StepVerifier.create(responseMono.flatMapMany(resp -> resp.getBody()))
                .assertNext(response -> {
                    assertEquals(AccountType.SAVINGS, response.getType());
                })
                .verifyComplete();
    }

    @Test
    void customersCustomerIdAccountsGet_wrapsBodyInOkResponseEntity() {
        when(accountUseCase.getAccountsByCustomer("customer-1"))
                .thenReturn(Flux.empty());

        Mono<org.springframework.http.ResponseEntity<Flux<AccountPolymorphicResponse>>> responseMono =
                delegate.customersCustomerIdAccountsGet("customer-1", mockExchange("/customers/customer-1/accounts"));

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCode().value());
                    assertNotNull(response.getBody());
                })
                .verifyComplete();

        verify(accountUseCase).getAccountsByCustomer("customer-1");
    }

    private SavingsAccount sampleSavingsAccount() {
        SavingsAccount account = new SavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100));
        account.setId("acc-1");
        return account;
    }

    private MockServerWebExchange mockExchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }
}
