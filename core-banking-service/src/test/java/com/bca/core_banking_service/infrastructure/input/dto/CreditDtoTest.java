package com.bca.core_banking_service.infrastructure.input.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class CreditDtoTest {

    @Test
    void constructorInitializesFields() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 10, 8, 30);
        Credit credit = new Credit(
                "cred-1",
                "customer-1",
                Credit.CreditType.MORTGAGE,
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(8000),
                BigDecimal.valueOf(8),
                36,
                Credit.CreditStatus.ACTIVE,
                createdAt, createdAt.plusMonths(36));

        assertEquals("cred-1", credit.getId());
        assertEquals("customer-1", credit.getCustomerId());
        assertEquals(Credit.CreditType.MORTGAGE, credit.getCreditType());
        assertEquals(BigDecimal.valueOf(10000), credit.getOriginalAmount());
        assertEquals(BigDecimal.valueOf(8000), credit.getPendingDebt());
        assertEquals(BigDecimal.valueOf(8), credit.getInterestRate());
        assertEquals(Integer.valueOf(36), credit.getTermMonths());
        assertEquals(Credit.CreditStatus.ACTIVE, credit.getStatus());
        assertEquals(createdAt, credit.getCreatedAt());
    }

    @Test
    void equalsAndHashCodeCompareAllFields() {
        LocalDateTime createdAt = LocalDateTime.now();
        Credit first = sampleCredit(createdAt);
        Credit second = sampleCredit(createdAt);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setStatus(Credit.CreditStatus.PAID);
        assertNotEquals(first, second);
    }

    @Test
    void settersAllowMutations() {
        Credit credit = new Credit();
        LocalDateTime createdAt = LocalDateTime.now();
        credit.setId("cred-2");
        credit.setCustomerId("customer-2");
        credit.setCreditType(Credit.CreditType.AUTO_LOAN);
        credit.setOriginalAmount(BigDecimal.valueOf(4000));
        credit.setPendingDebt(BigDecimal.valueOf(3000));
        credit.setInterestRate(BigDecimal.valueOf(6));
        credit.setTermMonths(24);
        credit.setStatus(Credit.CreditStatus.DEFAULTED);
        credit.setCreatedAt(createdAt);

        assertEquals("cred-2", credit.getId());
        assertEquals(Credit.CreditType.AUTO_LOAN, credit.getCreditType());
        assertEquals(BigDecimal.valueOf(4000), credit.getOriginalAmount());
        assertEquals(BigDecimal.valueOf(3000), credit.getPendingDebt());
        assertEquals(BigDecimal.valueOf(6), credit.getInterestRate());
        assertEquals(Integer.valueOf(24), credit.getTermMonths());
        assertEquals(Credit.CreditStatus.DEFAULTED, credit.getStatus());
        assertEquals(createdAt, credit.getCreatedAt());
    }

    @Test
    void toStringContainsKeyInformation() {
        Credit credit = sampleCredit(LocalDateTime.now());
        String description = credit.toString();
        assertTrue(description.contains("cred-1"));
        assertTrue(description.contains("customer-1"));
    }

    private Credit sampleCredit(LocalDateTime createdAt) {
        return new Credit(
                "cred-1",
                "customer-1",
                Credit.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(4500),
                BigDecimal.valueOf(10),
                12,
                Credit.CreditStatus.ACTIVE,
                createdAt, createdAt.plusMonths(12));
    }
}
