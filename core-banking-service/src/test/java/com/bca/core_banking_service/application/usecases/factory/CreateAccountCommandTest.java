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
        CreateAccountCommand first = new CreateAccountCommand("c-1", AccountType.CHECKING, "EUR");
        CreateAccountCommand second = new CreateAccountCommand("c-1", AccountType.CHECKING, "EUR");
        CreateAccountCommand different = new CreateAccountCommand("c-2", AccountType.SAVINGS, "USD");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    void toStringContainsDetails() {
        CreateAccountCommand command = new CreateAccountCommand("cust", AccountType.PYME_CHECKING, "PEN");
        assertTrue(command.toString().contains("cust"));
        assertTrue(command.toString().contains("PYME_CHECKING"));
    }
}
