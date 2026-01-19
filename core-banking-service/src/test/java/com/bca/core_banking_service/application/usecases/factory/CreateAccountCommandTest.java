package com.bca.core_banking_service.application.usecases.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

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

    @Test
    void allArgsConstructor_setsLists() {
        List<String> holders = List.of("holder-1", "holder-2");
        List<String> signers = List.of("signer-1");

        CreateAccountCommand cmd = new CreateAccountCommand(
                "c4",
                CustomerType.PERSONAL,
                AccountType.SAVINGS,
                "EUR",
                holders,
                signers);

        assertEquals(holders, cmd.getHolders());
        assertEquals(signers, cmd.getAuthorizedSigners());
        assertFalse(cmd.isBusiness());
    }

    @Test
    void builder_acceptsLists() {
        List<String> holders = List.of("holder-1");
        List<String> signers = List.of("signer-1", "signer-2");

        CreateAccountCommand cmd = CreateAccountCommand.builder()
                .customerId("c5")
                .customerType(CustomerType.PYMEBUSINESS)
                .type(AccountType.CHECKING)
                .currency("USD")
                .holders(holders)
                .authorizedSigners(signers)
                .build();

        assertEquals(holders, cmd.getHolders());
        assertEquals(signers, cmd.getAuthorizedSigners());
        assertTrue(cmd.isBusiness());
    }

    @Test
    void dataAnnotations_generateSettersAndEquals() {
        CreateAccountCommand cmd = new CreateAccountCommand("c6", CustomerType.PERSONAL, AccountType.SAVINGS, "USD");
        cmd.setCurrency("EUR");
        cmd.setCustomerType(CustomerType.VIPPERSONAL);
        cmd.setHolders(List.of("h1"));
        cmd.setAuthorizedSigners(List.of("s1"));

        assertEquals("EUR", cmd.getCurrency());
        assertEquals(CustomerType.VIPPERSONAL, cmd.getCustomerType());
        assertEquals(List.of("h1"), cmd.getHolders());
        assertEquals(List.of("s1"), cmd.getAuthorizedSigners());

        CreateAccountCommand same = CreateAccountCommand.builder()
                .customerId("c6")
                .customerType(CustomerType.VIPPERSONAL)
                .type(AccountType.SAVINGS)
                .currency("EUR")
                .holders(List.of("h1"))
                .authorizedSigners(List.of("s1"))
                .build();

        assertEquals(same, cmd);

        CreateAccountCommand different = CreateAccountCommand.builder()
                .customerId("c7")
                .customerType(CustomerType.PERSONAL)
                .type(AccountType.SAVINGS)
                .currency("EUR")
                .holders(List.of("h1"))
                .authorizedSigners(List.of("s1"))
                .build();

        assertNotEquals(different, cmd);
    }

    @Test
    void builder_withoutValues_leavesFieldsNull() {
        CreateAccountCommand empty = CreateAccountCommand.builder().build();

        assertEquals(null, empty.getCustomerId());
        assertEquals(null, empty.getCustomerType());
        assertEquals(null, empty.getType());
        assertEquals(null, empty.getCurrency());
        assertEquals(null, empty.getHolders());
        assertEquals(null, empty.getAuthorizedSigners());
    }

    @Test
    void builder_returnsDistinctInstances() {
        CreateAccountCommand first = CreateAccountCommand.builder().customerId("a").build();
        CreateAccountCommand second = CreateAccountCommand.builder().customerId("b").build();

        assertNotEquals(first, second);
        assertEquals("a", first.getCustomerId());
        assertEquals("b", second.getCustomerId());
    }
}
