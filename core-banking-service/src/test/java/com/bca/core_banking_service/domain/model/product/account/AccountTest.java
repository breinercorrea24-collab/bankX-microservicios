package com.bca.core_banking_service.infrastructure.input.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.AccountEntity.AccountStatus;

class AccountTest {

    @Test
    @DisplayName("Debe sumar el monto al balance actual")
    void deposit_AddsAmountToBalance() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));

        account.deposit(BigDecimal.valueOf(50));

        assertEquals(0, BigDecimal.valueOf(150).compareTo(account.getBalance()));
    }

    @Test
    @DisplayName("Debe restar el monto del balance actual")
    void withdraw_SubtractsAmountFromBalance() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));

        account.withdraw(BigDecimal.valueOf(30));

        assertEquals(0, BigDecimal.valueOf(70).compareTo(account.getBalance()));
    }

    @Test
    @DisplayName("Debe verificar el funcionamiento de los constructores y Lombok")
    void constructorAndLombokTest() {
        // Test AllArgsConstructor
        Account account = new Account(
            "ID-1", "CUST-1", Account.AccountType.SAVINGS, 
            "12345", "USD", BigDecimal.TEN, AccountStatus.ACTIVE, true
        );

        assertAll(
            () -> assertEquals("ID-1", account.getId()),
            () -> assertEquals("CUST-1", account.getCustomerId()),
            () -> assertEquals(Account.AccountType.SAVINGS, account.getType()),
            () -> assertEquals("12345", account.getAccountNumber()),
            () -> assertEquals("USD", account.getCurrency()),
            () -> assertEquals(BigDecimal.TEN, account.getBalance()),
            () -> assertEquals(AccountStatus.ACTIVE, account.getStatus()),
            () -> assertTrue(account.isActive())
        );
    }

    @Test
    @DisplayName("Debe verificar igualdad de objetos (Equals/HashCode)")
    void equalsAndHashCodeTest() {
        Account acc1 = new Account();
        acc1.setId("1");
        
        Account acc2 = new Account();
        acc2.setId("1");

        assertEquals(acc1, acc2);
        assertEquals(acc1.hashCode(), acc2.hashCode());
        
        acc2.setId("2");
        assertNotEquals(acc1, acc2);
    }

    @Test
    @DisplayName("Debe verificar los valores del Enum AccountType")
    void enumAccountTypeTest() {
        Account.AccountType[] types = Account.AccountType.values();
        assertEquals(4, types.length);
        assertNotNull(Account.AccountType.valueOf("SAVINGS"));
        assertNotNull(Account.AccountType.valueOf("CURRENT"));
        assertNotNull(Account.AccountType.valueOf("CHECKING"));
        assertNotNull(Account.AccountType.valueOf("FIXED_TERM"));
    }
}