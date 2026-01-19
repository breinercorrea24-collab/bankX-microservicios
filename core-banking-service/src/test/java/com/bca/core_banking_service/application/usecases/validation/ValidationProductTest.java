package com.bca.core_banking_service.application.usecases.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.infrastructure.output.rest.ExternalCardsWebClientAdapter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ValidationProductTest {

    @Mock
    private ExternalCardsWebClientAdapter externalCardsClient;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository creditRepository;

    private ValidationProduct validationProduct;

    @BeforeEach
    void setUp() {
        lenient().when(creditRepository.hasOverdueCredits(any()))
                .thenReturn(Mono.just(false));

        validationProduct = new ValidationProduct(externalCardsClient, accountRepository, creditRepository);
    }

    @Test
    void validateAccountCreation_personalCustomerWithExistingAccount_fails() {
        Account existing = mock(Account.class);
        when(existing.getType()).thenReturn(AccountType.SAVINGS);
        when(accountRepository.findByCustomerId("cust")).thenReturn(Flux.fromIterable(List.of(existing)));

        StepVerifier.create(validationProduct.validateAccountCreation(
                "cust",
                AccountType.SAVINGS,
                CustomerType.PERSONAL))
                .expectErrorSatisfies(error -> assertEquals(
                        "Only one account of this type is allowed for personal customers", error.getMessage()))
                .verify();
    }

    @Test
    void validateAccountCreation_businessCustomerWithForbiddenType_fails() {
        StepVerifier.create(validationProduct.validateAccountCreation("biz", AccountType.SAVINGS,
                CustomerType.BUSINESS))
                .expectErrorSatisfies(error -> assertEquals("Business customer cannot open SAVINGS",
                        error.getMessage()))
                .verify();
    }

    @Test
    void validateAccountCreation_vipRequiresCreditCard() {
        when(externalCardsClient.hasCreditCard("vip"))
                .thenReturn(Mono.just(ResponseEntity.ok(Boolean.FALSE)));

        StepVerifier.create(validationProduct.validateAccountCreation("vip", AccountType.VIP_SAVINGS,
                CustomerType.VIPPERSONAL))
                .expectErrorSatisfies(error -> assertEquals("This product requires active credit card",
                        error.getMessage()))
                .verify();
    }

    @Test
    void validateAccountCreation_pymeRequiresCreditCard() {
        when(externalCardsClient.hasCreditCard("pyme"))
                .thenReturn(Mono.just(ResponseEntity.ok(Boolean.FALSE)));

        StepVerifier.create(validationProduct.validateAccountCreation("pyme", AccountType.PYME_CHECKING,
                CustomerType.PYMEBUSINESS))
                .expectErrorSatisfies(error -> assertEquals("This product requires active credit card",
                        error.getMessage()))
                .verify();
    }

    @Test
    void validateAccountCreation_allowsValidCombination() {
        when(accountRepository.findByCustomerId("cust")).thenReturn(Flux.empty());

        StepVerifier.create(validationProduct.validateAccountCreation(
                "cust",
                AccountType.CHECKING,
                CustomerType.PERSONAL))
                .verifyComplete();
    }

    @Test
    void validateAccountCreation_whenCustomerHasOverdueCredit_failsImmediately() {
        // TODO : CORREGIR TESTS
        ValidationProduct validationProductSpy = new ValidationProduct(externalCardsClient, accountRepository, creditRepository) {
            @Override
            protected Mono<Boolean> hasOverdueCredits(String customerId) {
                return Mono.just(true);
            }
        };

        StepVerifier.create(validationProductSpy.validateAccountCreation(
                "debtor",
                AccountType.SAVINGS,
                CustomerType.PERSONAL))
                .expectErrorSatisfies(error -> assertEquals("Customer has overdue credit debt", error.getMessage()))
                .verify();
    }

    @Test
    void validateAccountCreation_withUnknownCustomerTypeReturnsEmpty() {
        StepVerifier.create(validationProduct.validateAccountCreation(
                "other",
                AccountType.SAVINGS,
                null))
                .expectError(NullPointerException.class)
                .verify();

        verifyNoInteractions(accountRepository);
    }
}
