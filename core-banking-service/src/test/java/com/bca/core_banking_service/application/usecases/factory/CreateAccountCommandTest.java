package com.bca.core_banking_service.application.usecases.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
