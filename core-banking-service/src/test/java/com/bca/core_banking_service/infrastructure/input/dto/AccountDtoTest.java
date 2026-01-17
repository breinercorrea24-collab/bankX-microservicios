package com.bca.core_banking_service.infrastructure.input.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.infrastructure.output.persistence.entity.AccountEntity.AccountStatus;

class AccountDtoTest {

    @Test
    void depositAddsToBalance() {
        Account account = sampleAccount();
        account.deposit(BigDecimal.valueOf(25));

        assertEquals(BigDecimal.valueOf(125), account.getBalance());
    }

    @Test
    void withdrawSubtractsFromBalance() {
        Account account = sampleAccount();
        account.withdraw(BigDecimal.valueOf(40));

        assertEquals(BigDecimal.valueOf(60), account.getBalance());
    }

    @Test
    void equalsAndHashCodeConsiderFields() {
        Account first = sampleAccount();
        Account second = sampleAccount();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setBalance(BigDecimal.valueOf(500));
        assertNotEquals(first, second);
    }

    @Test
    void settersAllowMutation() {
        Account account = new Account();
        account.setId("acc-2");
        account.setCustomerId("customer-2");
        account.setType(Account.AccountType.CHECKING);
        account.setAccountNumber("ACC-2");
        account.setCurrency("EUR");
        account.setBalance(BigDecimal.valueOf(50));
        account.setStatus(AccountStatus.INACTIVE);
        account.setActive(false);

        assertEquals("acc-2", account.getId());
        assertEquals(Account.AccountType.CHECKING, account.getType());
        assertEquals("ACC-2", account.getAccountNumber());
        assertEquals("EUR", account.getCurrency());
        assertEquals(BigDecimal.valueOf(50), account.getBalance());
        assertEquals(AccountStatus.INACTIVE, account.getStatus());
        assertEquals(false, account.isActive());
    }

    private Account sampleAccount() {
        return new Account(
                "acc-1",
                "customer-1",
                Account.AccountType.SAVINGS,
                "ACC-1",
                "USD",
                BigDecimal.valueOf(100),
                AccountStatus.ACTIVE,
                true);
    }
}
