package com.bca.core_banking_service.application.usecases.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;

class CreateAccountCommandTest {

    @Test
    void builder_setsFields() {
        CreateAccountCommand cmd = CreateAccountCommand.builder()
                .customerId("cust-1")
                .customerType(CustomerType.BUSINESS)
                .type(AccountType.PYME_CHECKING)
                .currency("USD")
                .build();

        assertEquals("cust-1", cmd.getCustomerId());
        assertEquals(CustomerType.BUSINESS, cmd.getCustomerType());
        assertEquals(AccountType.PYME_CHECKING, cmd.getType());
        assertEquals("USD", cmd.getCurrency());
        assertTrue(cmd.isBusiness());
    }

    @Test
    void isBusiness_returnsTrueForBusinessTypes() {
        CreateAccountCommand businessCmd = new CreateAccountCommand("c1", CustomerType.BUSINESS, AccountType.CHECKING, "USD");
        CreateAccountCommand pymeCmd = new CreateAccountCommand("c2", CustomerType.PYMEBUSINESS, AccountType.CHECKING, "USD");
        CreateAccountCommand personalCmd = new CreateAccountCommand("c3", CustomerType.PERSONAL, AccountType.CHECKING, "USD");

        assertTrue(businessCmd.isBusiness());
        assertTrue(pymeCmd.isBusiness());
        assertFalse(personalCmd.isBusiness());
    }
}
