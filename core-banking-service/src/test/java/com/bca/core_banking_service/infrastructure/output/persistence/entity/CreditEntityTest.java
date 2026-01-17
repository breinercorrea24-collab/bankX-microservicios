package com.bca.core_banking_service.infrastructure.output.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class CreditEntityTest {

    @Test
    void constructorAndGettersWork() {
        LocalDateTime createdAt = LocalDateTime.now();
        CreditEntity entity = new CreditEntity(
                "cred-1",
                "customer-1",
                CreditEntity.CreditType.PERSONAL_LOAN,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(800),
                BigDecimal.valueOf(10),
                12,
                CreditEntity.CreditStatus.ACTIVE,
                createdAt, LocalDateTime.now().plusMonths(12));

        assertEquals("cred-1", entity.getId());
        assertEquals("customer-1", entity.getCustomerId());
        assertEquals(CreditEntity.CreditType.PERSONAL_LOAN, entity.getCreditType());
        assertEquals(BigDecimal.valueOf(1000), entity.getOriginalAmount());
        assertEquals(BigDecimal.valueOf(800), entity.getPendingDebt());
        assertEquals(BigDecimal.valueOf(10), entity.getInterestRate());
        assertEquals(12, entity.getTermMonths());
        assertEquals(CreditEntity.CreditStatus.ACTIVE, entity.getStatus());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void settersAllowMutations() {
        CreditEntity entity = new CreditEntity();
        LocalDateTime createdAt = LocalDateTime.now();
        entity.setId("cred-2");
        entity.setCustomerId("customer-2");
        entity.setCreditType(CreditEntity.CreditType.AUTO_LOAN);
        entity.setOriginalAmount(BigDecimal.valueOf(5000));
        entity.setPendingDebt(BigDecimal.valueOf(4500));
        entity.setInterestRate(BigDecimal.valueOf(12));
        entity.setTermMonths(24);
        entity.setStatus(CreditEntity.CreditStatus.DEFAULTED);
        entity.setCreatedAt(createdAt);

        assertEquals("cred-2", entity.getId());
        assertEquals("customer-2", entity.getCustomerId());
        assertEquals(CreditEntity.CreditType.AUTO_LOAN, entity.getCreditType());
        assertEquals(BigDecimal.valueOf(5000), entity.getOriginalAmount());
        assertEquals(BigDecimal.valueOf(4500), entity.getPendingDebt());
        assertEquals(BigDecimal.valueOf(12), entity.getInterestRate());
        assertEquals(24, entity.getTermMonths());
        assertEquals(CreditEntity.CreditStatus.DEFAULTED, entity.getStatus());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void equalsAndHashCodeIncludeFields() {
        CreditEntity first = sampleEntity("cred-3");
        CreditEntity second = sampleEntity("cred-3");
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        CreditEntity different = sampleEntity("cred-4");
        assertNotEquals(first, different);
    }

    @Test
    void toStringContainsKeyFields() {
        CreditEntity entity = sampleEntity("cred-5");
        String toString = entity.toString();
        assertTrue(toString.contains("cred-5"));
        assertTrue(toString.contains("customer-5"));
    }

    private CreditEntity sampleEntity(String id) {
        CreditEntity entity = new CreditEntity();
        entity.setId(id);
        entity.setCustomerId("customer-5");
        entity.setCreditType(CreditEntity.CreditType.MORTGAGE);
        entity.setOriginalAmount(BigDecimal.TEN);
        entity.setPendingDebt(BigDecimal.ONE);
        entity.setInterestRate(BigDecimal.valueOf(3));
        entity.setTermMonths(36);
        entity.setStatus(CreditEntity.CreditStatus.ACTIVE);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
