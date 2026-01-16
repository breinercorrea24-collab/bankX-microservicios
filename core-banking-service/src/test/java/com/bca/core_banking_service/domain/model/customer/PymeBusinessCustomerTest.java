package com.bca.core_banking_service.domain.model.customer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PymeBusinessCustomerTest {

    @Test
    void inheritsBusinessCapabilities() {
        PymeBusinessCustomer customer = new PymeBusinessCustomer();

        assertTrue(customer.canCreateAccount());
        assertTrue(customer.canCreateCredit());
    }
}
