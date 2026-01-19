package com.bca.core_banking_service.domain.model.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.credit.CreditStatus;
import com.bca.core_banking_service.domain.model.enums.credit.CreditType;

class PersonalLoanCreditTest {

    @Test
    void constructor_initializesFields() {
        LocalDateTime before = LocalDateTime.now();
        PersonalLoanCredit credit = new PersonalLoanCredit(
                "cust-1",
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(12),
                12);
        LocalDateTime after = LocalDateTime.now();

        assertEquals("cust-1", credit.getCustomerId());
        assertEquals(CreditType.PERSONAL_LOAN, credit.getCreditType());
        assertEquals(BigDecimal.valueOf(1200), credit.getOriginalAmount());
        assertEquals(BigDecimal.valueOf(1200), credit.getPendingDebt());
        assertEquals(BigDecimal.valueOf(12), credit.getInterestRate());
        assertEquals(BigDecimal.valueOf(1200), credit.getCreditLimit());
        assertEquals(12, credit.getTermMonths());
        assertEquals(CreditStatus.ACTIVE, credit.getStatus());
        // createdAt y dueDate dentro del rango esperado
        assertNotEquals(null, credit.getCreatedAt());
        assertNotEquals(null, credit.getDueDate());
        assertEquals(true, !credit.getCreatedAt().isBefore(before) && !credit.getCreatedAt().isAfter(after.plusSeconds(1)));
        assertEquals(true, credit.getDueDate().isAfter(credit.getCreatedAt()));
    }

    @Test
    void calculateInstallment_returnsMonthlyPayment() {
        PersonalLoanCredit credit = new PersonalLoanCredit(
                "cust-1",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(12), // 12% anual
                12);

        BigDecimal installment = credit.calculateInstallment();

        // Valor esperado con fórmula simple de anualidad
        assertEquals(new BigDecimal("88.85"), installment);
    }

    @Test
    void calculateInstallment_throwsWhenTermMonthsInvalid() {
        PersonalLoanCredit credit = new PersonalLoanCredit(
                "cust-1",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(10),
                1);
        credit.setTermMonths(0);

        assertThrows(IllegalArgumentException.class, credit::calculateInstallment);
    }
}
