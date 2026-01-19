package com.bca.core_banking_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class CreditUseCaseImplTest {

    @Mock
    private CreditRepository creditRepository;
    @Mock
    private com.bca.core_banking_service.application.usecases.validation.ValidationCredit validationCredit;

    private CreditUseCaseImpl creditUseCase;

    @BeforeEach
    void setUp() {
        creditUseCase = new CreditUseCaseImpl(creditRepository, validationCredit);
        lenient().when(validationCredit.validateCreditCreation(any(), any(), any()))
                .thenReturn(Mono.empty());
    }

    @Test
    void createCredit_whenAmountBelowLimit_persistsCredit() {
        when(creditRepository.save(any(Credit.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(creditUseCase.createCredit(
                "customer-1",
                Credit.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000),
                12,
                BigDecimal.valueOf(5)))
                .assertNext(credit -> {
                    assertEquals("customer-1", credit.getCustomerId());
                    assertEquals(Credit.CreditType.PERSONAL_LOAN, credit.getCreditType());
                    assertEquals(BigDecimal.valueOf(1000), credit.getOriginalAmount());
                    assertEquals(12, credit.getTermMonths());
                    assertEquals(Credit.CreditStatus.ACTIVE, credit.getStatus());
                    assertEquals(credit.getOriginalAmount(), credit.getPendingDebt());
                })
                .verifyComplete();

        ArgumentCaptor<Credit> captor = ArgumentCaptor.forClass(Credit.class);
        verify(creditRepository).save(captor.capture());
        assertEquals("customer-1", captor.getValue().getCustomerId());
    }

    @Test
    void createCredit_whenAmountExceedsLimit_emitsError() {
        StepVerifier.create(creditUseCase.createCredit(
                "customer-1",
                Credit.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(100_000),
                24,
                BigDecimal.TEN))
                .expectErrorSatisfies(error -> assertTrue(error.getMessage().contains("exceeds")))
                .verify();

        verify(creditRepository, never()).save(any(Credit.class));
    }

    @Test
    void createCredit_whenTermIsNull_alignsDueDateWithCreation() {
        when(creditRepository.save(any(Credit.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(creditUseCase.createCredit(
                "customer-2",
                Credit.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1500),
                null,
                BigDecimal.valueOf(4)))
                .assertNext(credit -> {
                    assertEquals("customer-2", credit.getCustomerId());
                    assertEquals(Credit.CreditType.PERSONAL_LOAN, credit.getCreditType());
                    assertEquals(BigDecimal.valueOf(1500), credit.getOriginalAmount());
                    assertEquals(BigDecimal.valueOf(1500), credit.getPendingDebt());
                    assertEquals(Credit.CreditStatus.ACTIVE, credit.getStatus());
                    assertEquals(credit.getCreatedAt(), credit.getDueDate());
                    assertEquals(null, credit.getTermMonths());
                })
                .verifyComplete();

        verify(creditRepository).save(any(Credit.class));
    }

    @Test
    void payCredit_whenAmountIsValid_updatesPendingDebt() {
        Credit existing = buildCredit("cred-1", BigDecimal.valueOf(1000));
        when(creditRepository.findById("cred-1")).thenReturn(Mono.just(existing));
        when(creditRepository.save(any(Credit.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(creditUseCase.payCredit("cred-1", BigDecimal.valueOf(400)))
                .assertNext(updated -> assertEquals(BigDecimal.valueOf(600), updated.getPendingDebt()))
                .verifyComplete();

        ArgumentCaptor<Credit> captor = ArgumentCaptor.forClass(Credit.class);
        verify(creditRepository).save(captor.capture());
        assertEquals(BigDecimal.valueOf(600), captor.getValue().getPendingDebt());
    }

    @Test
    void payCredit_whenPaymentExceedsDebt_emitsError() {
        Credit existing = buildCredit("cred-2", BigDecimal.valueOf(300));
        when(creditRepository.findById("cred-2")).thenReturn(Mono.just(existing));

        StepVerifier.create(creditUseCase.payCredit("cred-2", BigDecimal.valueOf(500)))
                .expectErrorSatisfies(error -> assertTrue(error.getMessage().contains("exceeds pending debt")))
                .verify();

        verify(creditRepository, never()).save(any(Credit.class));
    }

    @Test
    void payCredit_whenDebtReachesZero_marksAsPaid() {
        Credit existing = buildCredit("cred-3", BigDecimal.valueOf(250));
        when(creditRepository.findById("cred-3")).thenReturn(Mono.just(existing));
        when(creditRepository.save(any(Credit.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(creditUseCase.payCredit("cred-3", BigDecimal.valueOf(250)))
                .assertNext(updated -> assertEquals(Credit.CreditStatus.PAID, updated.getStatus()))
                .verifyComplete();

        verify(creditRepository).save(existing);
    }

    private Credit buildCredit(String id, BigDecimal pendingDebt) {
        Credit credit = new Credit();
        credit.setId(id);
        credit.setCustomerId("customer");
        credit.setCreditType(Credit.CreditType.PERSONAL_LOAN);
        credit.setOriginalAmount(pendingDebt);
        credit.setPendingDebt(pendingDebt);
        credit.setInterestRate(BigDecimal.ONE);
        credit.setTermMonths(12);
        credit.setStatus(Credit.CreditStatus.ACTIVE);
        return credit;
    }
}
