package com.bca.core_banking_service.application.usecases.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.infrastructure.output.rest.ExternalCardsWebClientAdapter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ValidationCustomerTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ExternalCardsWebClientAdapter externalCardsClient;

    private ValidationCustomer validationCustomer;

    @BeforeEach
    void setUp() {
        validationCustomer = new ValidationCustomer(accountRepository, externalCardsClient);
    }

    @Test
    void validatePersonalCustomer_whenSameTypeExists_throwsBusinessException() {
        Account existing = mock(Account.class);
        when(existing.getType()).thenReturn(AccountType.SAVINGS);
        when(accountRepository.findByCustomerId("cust-1"))
                .thenReturn(Flux.fromIterable(List.of(existing)));

        StepVerifier.create(validationCustomer.validatePersonalCustomer("cust-1", AccountType.SAVINGS,
                CustomerType.PERSONAL))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals("Only one account of this type is allowed for personal customers",
                            error.getMessage());
                })
                .verify();
    }

    @Test
    void validatePersonalCustomer_whenDifferentTypeExists_allowsCreation() {
        Account existing = mock(Account.class);
        when(existing.getType()).thenReturn(AccountType.CHECKING);
        when(accountRepository.findByCustomerId("cust-1"))
                .thenReturn(Flux.fromIterable(List.of(existing)));

        StepVerifier.create(validationCustomer.validatePersonalCustomer("cust-1", AccountType.SAVINGS,
                CustomerType.PERSONAL))
                .verifyComplete();
    }

    @Test
    void validateBusinessCustomer_rejectsSavingsAndFixedTerm() {
        StepVerifier.create(validationCustomer.validateBusinessCustomer("cust", AccountType.SAVINGS,
                CustomerType.BUSINESS))
                .expectErrorSatisfies(error -> assertEquals("Business customer cannot open SAVINGS",
                        error.getMessage()))
                .verify();

        StepVerifier.create(validationCustomer.validateBusinessCustomer("cust", AccountType.FIXED_TERM,
                CustomerType.BUSINESS))
                .expectErrorSatisfies(error -> assertEquals("Business customer cannot open FIXED_TERM",
                        error.getMessage()))
                .verify();

        StepVerifier.create(validationCustomer.validateBusinessCustomer("cust", AccountType.CHECKING,
                CustomerType.BUSINESS))
                .verifyComplete();
    }

    @Test
    void validateVipPersonalCustomer_requiresCreditCard() {
        when(externalCardsClient.hasCreditCard("vip"))
                .thenReturn(Mono.just(ResponseEntity.ok(Boolean.TRUE)));

        StepVerifier.create(validationCustomer.validateVipPersonalCustomer("vip", AccountType.VIP_SAVINGS,
                CustomerType.VIPPERSONAL))
                .verifyComplete();

        when(externalCardsClient.hasCreditCard("vip"))
                .thenReturn(Mono.just(ResponseEntity.ok(Boolean.FALSE)));

        StepVerifier.create(validationCustomer.validateVipPersonalCustomer("vip", AccountType.VIP_SAVINGS,
                CustomerType.VIPPERSONAL))
                .expectErrorSatisfies(error -> assertEquals("This product requires active credit card",
                        error.getMessage()))
                .verify();
    }

    @Test
    void validatePymeBusinessCustomer_requiresCreditCard() {
        when(externalCardsClient.hasCreditCard("pyme"))
                .thenReturn(Mono.just(ResponseEntity.ok(Boolean.TRUE)));

        StepVerifier.create(validationCustomer.validatePymeBusinessCustomer("pyme", AccountType.PYME_CHECKING,
                CustomerType.PYMEBUSINESS))
                .verifyComplete();

        when(externalCardsClient.hasCreditCard("pyme"))
                .thenReturn(Mono.just(ResponseEntity.ok(Boolean.FALSE)));

        StepVerifier.create(validationCustomer.validatePymeBusinessCustomer("pyme", AccountType.PYME_CHECKING,
                CustomerType.PYMEBUSINESS))
                .expectErrorSatisfies(error -> assertEquals("This product requires active credit card",
                        error.getMessage()))
                .verify();
    }

    @Test
    void vipAndPymeValidations_ignoreOtherAccountTypes() {
        StepVerifier.create(validationCustomer.validateVipPersonalCustomer("vip", AccountType.SAVINGS,
                CustomerType.VIPPERSONAL))
                .verifyComplete();

        StepVerifier.create(validationCustomer.validatePymeBusinessCustomer("pyme", AccountType.CHECKING,
                CustomerType.PYMEBUSINESS))
                .verifyComplete();
    }
}
