package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class AccountTest {

    @Test
    void depositAddsAmount() {
        TestAccount account = new TestAccount();
        account.setBalance(BigDecimal.valueOf(100));

        account.deposit(BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), account.getBalance());
    }

    @Test
    void depositWithInvalidAmountThrows() {
        TestAccount account = new TestAccount();
        account.setBalance(BigDecimal.TEN);

        assertThrows(RuntimeException.class, () -> account.deposit(BigDecimal.ZERO));
    }

    @Test
    void withdrawSubtractsAmount() {
        TestAccount account = new TestAccount();
        account.setBalance(BigDecimal.valueOf(200));

        account.withdraw(BigDecimal.valueOf(75));

        assertEquals(BigDecimal.valueOf(125), account.getBalance());
    }

    @Test
    void withdrawWithInsufficientBalanceThrows() {
        TestAccount account = new TestAccount();
        account.setBalance(BigDecimal.valueOf(40));

        assertThrows(RuntimeException.class, () -> account.withdraw(BigDecimal.valueOf(50)));
    }

    private static class TestAccount extends Account {
        TestAccount() {
            super("customer-1", "USD", ProductStatus.ACTIVE, BigDecimal.ZERO);
            this.type = AccountType.SAVINGS;
        }

        @Override
        public boolean hasMaintenanceFee() {
            return false;
        }

        @Override
        public void validateCreation() {
            // not required
        }
    }
}
