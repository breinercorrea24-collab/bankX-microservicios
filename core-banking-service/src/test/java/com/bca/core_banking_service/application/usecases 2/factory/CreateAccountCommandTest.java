package com.bca.core_banking_service.application.usecases.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;

class CreateAccountCommandTest {

    @Test
    void constructorPopulatesFields() {
        CreateAccountCommand command = new CreateAccountCommand("customer-123", AccountType.SAVINGS, "USD");

        assertEquals("customer-123", command.getCustomerId());
        assertEquals(AccountType.SAVINGS, command.getType());
        assertEquals("USD", command.getCurrency());
    }

    @Test
    void equalsAndHashCodeConsiderFields() {
        CreateAccountCommand first = CreateAccountCommand.builder()
                .customerId("c-1")
                .type(AccountType.CHECKING)
                .currency("EUR")
                .build();
        CreateAccountCommand second = CreateAccountCommand.builder()
                .customerId("c-1")
                .type(AccountType.CHECKING)
                .currency("EUR")
                .build();
        CreateAccountCommand different = CreateAccountCommand.builder()
                .customerId("c-2")
                .type(AccountType.SAVINGS)
                .currency("USD")
                .build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    void toStringContainsDetails() {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .customerId("cust")
                .type(AccountType.PYME_CHECKING)
                .currency("PEN")
                .build();
        assertTrue(command.toString().contains("cust"));
        assertTrue(command.toString().contains("PYME_CHECKING"));
    }
}
