package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void equalsHandlesNullMinimumDailyAverage() {
        VipSavingsAccount first = createAccount();
        first.setMinimumDailyAverage(null);
        VipSavingsAccount second = createAccount();
        second.setMinimumDailyAverage(null);
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);

        second.setMinimumDailyAverage(BigDecimal.ONE);
        assertNotEquals(first, second);
        assertNotEquals(first, new Object());
    }

    @Test
    void toStringContainsMinimumDailyAverage() {
        VipSavingsAccount account = createAccount();
        String description = account.toString();
        assertTrue(description.contains("VipSavingsAccount"));
        assertTrue(description.contains("minimumDailyAverage=5000"));
    }

    @Test
    void noArgsConstructorAllowsSettingFields() {
        VipSavingsAccount account = new VipSavingsAccount();
        account.setCustomerId("cust");
        account.setCurrency("USD");
        account.setMinimumDailyAverage(BigDecimal.TEN);

        assertEquals("cust", account.getCustomerId());
        assertEquals(BigDecimal.TEN, account.getMinimumDailyAverage());
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
