package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class VipSavingsAccountTest {

    @Test
    void constructorSetsMinimumDailyAverage() {
        VipSavingsAccount account = new VipSavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.VIP_SAVINGS,
                5,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(5000));

        assertEquals(BigDecimal.valueOf(5000), account.getMinimumDailyAverage());
    }

    @Test
    void equalsAndHashCodeIncludeFields() {
        VipSavingsAccount first = createAccount();
        VipSavingsAccount second = createAccount();
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        VipSavingsAccount different = createAccount();
        different.setMinimumDailyAverage(BigDecimal.ZERO);
        assertNotEquals(first, different);
    }

    private VipSavingsAccount createAccount() {
        return new VipSavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.VIP_SAVINGS,
                5,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(5000));
    }
}
