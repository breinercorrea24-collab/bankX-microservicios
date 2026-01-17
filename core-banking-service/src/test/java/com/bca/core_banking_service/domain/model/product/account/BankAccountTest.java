package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;

class BankAccountTest {

    @Test
    void constructorAllowsSettingProtectedFields() {
        TestBankAccount account = new TestBankAccount("cust-1", BigDecimal.valueOf(100), Currency.getInstance("USD"));

        assertEquals("cust-1", account.customerId);
        assertEquals(BigDecimal.valueOf(100), account.balance);
        assertEquals(Currency.getInstance("USD"), account.currency);
        assertEquals(5, account.freeTransactionLimit);
        assertEquals(BigDecimal.ONE, account.commissionPerExtra);
    }

    @Test
    void depositAndWithdrawUpdateBalance() {
        TestBankAccount account = new TestBankAccount("cust-1", BigDecimal.valueOf(100), Currency.getInstance("USD"));

        account.deposit(BigDecimal.valueOf(25));
        assertEquals(BigDecimal.valueOf(125), account.balance);

        account.withdraw(BigDecimal.valueOf(40));
        assertEquals(BigDecimal.valueOf(85), account.balance);
    }

    private static final class TestBankAccount extends BankAccount {
        private TestBankAccount(String customerId, BigDecimal balance, Currency currency) {
            this.customerId = customerId;
            this.balance = balance;
            this.currency = currency;
            this.freeTransactionLimit = 5;
            this.commissionPerExtra = BigDecimal.ONE;
        }

        @Override
        public void deposit(BigDecimal amount) {
            balance = balance.add(amount);
        }

        @Override
        public void withdraw(BigDecimal amount) {
            balance = balance.subtract(amount);
        }
    }
}
