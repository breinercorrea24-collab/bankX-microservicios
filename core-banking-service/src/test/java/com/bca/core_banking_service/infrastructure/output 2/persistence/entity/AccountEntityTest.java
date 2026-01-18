package com.bca.core_banking_service.infrastructure.output.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class AccountEntityTest {

    @Test
    void allArgsConstructorPopulatesFields() {
        AccountEntity entity = new AccountEntity(
                "acc-1",
                "customer-1",
                AccountEntity.AccountType.SAVINGS,
                "USD",
                BigDecimal.valueOf(500),
                AccountEntity.AccountStatus.ACTIVE,
                true);

        assertEquals("acc-1", entity.getId());
        assertEquals("customer-1", entity.getCustomerId());
        assertEquals(AccountEntity.AccountType.SAVINGS, entity.getType());
        assertEquals("USD", entity.getCurrency());
        assertEquals(BigDecimal.valueOf(500), entity.getBalance());
        assertEquals(AccountEntity.AccountStatus.ACTIVE, entity.getStatus());
        assertTrue(entity.isActive());
    }

    @Test
    void settersAllowMutationWhenUsingNoArgsConstructor() {
        AccountEntity entity = new AccountEntity();
        entity.setId("acc-2");
        entity.setCustomerId("customer-2");
        entity.setType(AccountEntity.AccountType.CURRENT);
        entity.setCurrency("EUR");
        entity.setBalance(BigDecimal.valueOf(250));
        entity.setStatus(AccountEntity.AccountStatus.INACTIVE);
        entity.setActive(false);

        assertEquals("acc-2", entity.getId());
        assertEquals("customer-2", entity.getCustomerId());
        assertEquals(AccountEntity.AccountType.CURRENT, entity.getType());
        assertEquals("EUR", entity.getCurrency());
        assertEquals(BigDecimal.valueOf(250), entity.getBalance());
        assertEquals(AccountEntity.AccountStatus.INACTIVE, entity.getStatus());
        assertEquals(false, entity.isActive());
    }

    @Test
    void equalsAndHashCodeConsiderFields() {
        AccountEntity first = sampleEntity("acc-3");
        AccountEntity second = sampleEntity("acc-3");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        AccountEntity different = sampleEntity("acc-4");
        assertNotEquals(first, different);
    }

    @Test
    void toStringContainsKeyInformation() {
        AccountEntity entity = sampleEntity("acc-5");
        String description = entity.toString();
        assertTrue(description.contains("acc-5"));
        assertTrue(description.contains("customer-5"));
    }

    private AccountEntity sampleEntity(String id) {
        AccountEntity entity = new AccountEntity();
        entity.setId(id);
        entity.setCustomerId("customer-5");
        entity.setType(AccountEntity.AccountType.SAVINGS);
        entity.setCurrency("USD");
        entity.setBalance(BigDecimal.valueOf(1000));
        entity.setStatus(AccountEntity.AccountStatus.ACTIVE);
        entity.setActive(true);
        return entity;
    }
}
