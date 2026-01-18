package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class SavingsAccountTest {

    @Test
    void validateMonthlyLimit_allowsWhenUnderLimit() {
        SavingsAccount account = createAccount();
        account.setCurrentTransactions(4);

        assertDoesNotThrow(account::validateMonthlyLimit);
    }

    @Test
    void validateMonthlyLimit_throwsWhenLimitReached() {
        SavingsAccount account = createAccount();
        account.setCurrentTransactions(5);

        assertThrows(RuntimeException.class, account::validateMonthlyLimit);
    }

    @Test
    void validateCreation_isNotYetImplemented() {
        SavingsAccount account = createAccount();

        assertThrows(UnsupportedOperationException.class, account::validateCreation);
    }

    @Test
    void equalsAndHashCodeIncludeFields() {
        SavingsAccount first = createAccount();
        SavingsAccount second = createAccount();
        second.setCreatedAt(first.getCreatedAt());
        second.setId(first.getId());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        SavingsAccount different = createAccount();
        different.setMaintenanceCommission(BigDecimal.TEN);
        assertNotEquals(first, different);
    }

    private SavingsAccount createAccount() {
        SavingsAccount account = new SavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ONE,
                BigDecimal.valueOf(100));
        account.setCurrentTransactions(0);
        return account;
    }
}
