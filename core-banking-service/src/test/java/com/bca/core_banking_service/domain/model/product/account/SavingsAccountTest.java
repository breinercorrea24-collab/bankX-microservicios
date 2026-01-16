package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
