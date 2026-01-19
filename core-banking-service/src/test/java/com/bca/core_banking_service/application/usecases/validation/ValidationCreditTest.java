package com.bca.core_banking_service.application.usecases.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.enums.credit.CreditType;
import com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ValidationCreditTest {

    @Mock
    private CreditRepository creditRepository;

    private ValidationCredit validationCredit;

    @BeforeEach
    void setUp() {
        validationCredit = new ValidationCredit(creditRepository);
    }

    @Test
    void validateCreditCreation_personalWithoutExistingCredit_succeeds() {
        when(creditRepository.countByCustomerId("cust-1")).thenReturn(Mono.just(0L));

        StepVerifier.create(validationCredit.validateCreditCreation(
                "cust-1",
                CustomerType.PERSONAL,
                CreditType.PERSONAL_LOAN))
                .verifyComplete();

        verify(creditRepository).countByCustomerId("cust-1");
    }

    @Test
    void validateCreditCreation_personalWithExistingCredit_emitsBusinessException() {
        when(creditRepository.countByCustomerId("cust-2")).thenReturn(Mono.just(1L));

        StepVerifier.create(validationCredit.validateCreditCreation(
                "cust-2",
                CustomerType.PERSONAL,
                CreditType.PERSONAL_LOAN))
                .expectErrorSatisfies(error -> assertTrue(error instanceof BusinessException))
                .verify();

        verify(creditRepository).countByCustomerId("cust-2");
    }

    @Test
    void validateCreditCreation_businessCustomer_allowsWithoutChecks() {
        StepVerifier.create(validationCredit.validateCreditCreation(
                "biz-1",
                CustomerType.BUSINESS,
                CreditType.BUSINESS_LOAN))
                .verifyComplete();

        verify(creditRepository, never()).countByCustomerId(anyString());
    }

    @Test
    void validateCreditCreation_unknownCustomerType_emitsBusinessException() {
        StepVerifier.create(validationCredit.validateCreditCreation(
                "cust-3",
                CustomerType.VIPPERSONAL,
                CreditType.PERSONAL_LOAN))
                .expectError(BusinessException.class)
                .verify();

        verify(creditRepository, never()).countByCustomerId(anyString());
    }
}
