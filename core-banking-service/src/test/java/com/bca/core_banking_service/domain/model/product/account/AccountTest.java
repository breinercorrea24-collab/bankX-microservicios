package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void equalsAndHashCodeConsiderEveryField() {
        TestAccount first = configuredAccount();
        TestAccount second = configuredAccount();
        second.setCreatedAt(first.getCreatedAt());
        second.setAccountNumber(first.getAccountNumber());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setCommissionPerExtra(BigDecimal.valueOf(3));
        assertNotEquals(first, second);
    }

    @Test
    void toStringIncludesImportantInformation() {
        TestAccount account = configuredAccount();

        String asText = account.toString();
        assertTrue(asText.contains("ACC-1"));
        assertTrue(asText.contains("USD"));
        assertTrue(asText.contains("type=" + AccountType.SAVINGS));
    }

    @Test
    void gettersAndSettersExposeState() {
        TestAccount account = new TestAccount();
        account.setAccountNumber("ACC-1");
        account.setBalance(BigDecimal.valueOf(200));
        account.setCurrency("EUR");
        account.setType(AccountType.CHECKING);
        account.setFreeTransactionLimit(5);
        account.setCommissionPerExtra(BigDecimal.ONE);
        account.setInitialDeposit(BigDecimal.valueOf(50));

        assertEquals("ACC-1", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(200), account.getBalance());
        assertEquals("EUR", account.getCurrency());
        assertEquals(AccountType.CHECKING, account.getType());
        assertEquals(5, account.getFreeTransactionLimit());
        assertEquals(BigDecimal.ONE, account.getCommissionPerExtra());
        assertEquals(BigDecimal.valueOf(50), account.getInitialDeposit());
    }

    @Test
    void accountStatusEnumProvidesExpectedValues() {
        Account.AccountStatus[] statuses = Account.AccountStatus.values();
        assertEquals(2, statuses.length);
        assertEquals(Account.AccountStatus.ACTIVE, Account.AccountStatus.valueOf("ACTIVE"));
        assertEquals(Account.AccountStatus.INACTIVE, Account.AccountStatus.valueOf("INACTIVE"));
    }

    @Test
    void equalsDetectsDifferentCurrencyAndAccountNumber() {
        TestAccount first = configuredAccount();
        TestAccount second = configuredAccount();
        second.setCurrency("EUR");
        assertNotEquals(first, second);

        second = configuredAccount();
        second.setAccountNumber("OTHER");
        assertNotEquals(first, second);
    }

    @Test
    void equalsHandlesNullBalanceAgainstValue() {
        TestAccount first = configuredAccount();
        first.setBalance(null);
        TestAccount second = configuredAccount();
        second.setBalance(null);
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);

        second.setBalance(BigDecimal.ONE);
        assertNotEquals(first, second);
    }

    private TestAccount configuredAccount() {
        TestAccount account = new TestAccount();
        account.setAccountNumber("ACC-1");
        account.setBalance(BigDecimal.valueOf(150));
        account.setCurrency("USD");
        account.setType(AccountType.SAVINGS);
        account.setFreeTransactionLimit(10);
        account.setCommissionPerExtra(BigDecimal.valueOf(2));
        account.setInitialDeposit(BigDecimal.TEN);
        return account;
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
