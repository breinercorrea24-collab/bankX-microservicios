package com.bca.core_banking_service.domain.model.customer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PersonalCustomerTest {

    @Test
    void personalCustomerAllowsAccountAndCredit() {
        PersonalCustomer customer = new PersonalCustomer();

        assertTrue(customer.canCreateAccount());
        assertTrue(customer.canCreateCredit());
    }
}
