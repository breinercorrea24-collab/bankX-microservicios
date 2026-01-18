package com.bca.core_banking_service.infrastructure.input.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;

import com.bca.core_banking_service.application.ports.input.usecases.AccountUseCase;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.dto.AccountPolymorphicResponse;
import com.bca.core_banking_service.dto.SavingsAccountResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CustomerApiDelegateImplTest {

    @Mock
    private AccountUseCase accountUseCase;

    @Mock
    private ServerWebExchange exchange;

    private CustomerApiDelegateImpl delegate;

    @BeforeEach
    void setUp() {
        delegate = new CustomerApiDelegateImpl(accountUseCase);
    }

    @Test
    void customersCustomerIdAccountsGet_returnsMappedFluxResponse() {
        SavingsAccount account = new SavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ZERO,
                BigDecimal.valueOf(150));
        account.setId("acc-1");

        when(accountUseCase.getAccountsByCustomer("customer-1"))
                .thenReturn(Flux.just(account));

        Mono<ResponseEntity<Flux<AccountPolymorphicResponse>>> result =
                delegate.customersCustomerIdAccountsGet("customer-1", exchange);

        ResponseEntity<Flux<AccountPolymorphicResponse>> response = result.block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<AccountPolymorphicResponse> payload = response.getBody().collectList().block();
        assertNotNull(payload);
        assertEquals(1, payload.size());
        assertTrue(payload.get(0) instanceof SavingsAccountResponse);

        SavingsAccountResponse dto = (SavingsAccountResponse) payload.get(0);
        assertEquals("acc-1", dto.getId());
        assertEquals("customer-1", dto.getCustomerId());
        assertEquals(AccountType.SAVINGS, dto.getType());

        verify(accountUseCase).getAccountsByCustomer("customer-1");
    }
}
