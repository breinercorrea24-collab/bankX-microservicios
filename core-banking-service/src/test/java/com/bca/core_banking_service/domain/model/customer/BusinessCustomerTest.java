package com.bca.core_banking_service.domain.model.customer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BusinessCustomerTest {

    @Test
    void businessCustomerAllowsAccountAndCreditCreation() {
        BusinessCustomer customer = new BusinessCustomer();

        assertTrue(customer.canCreateAccount());
        assertTrue(customer.canCreateCredit());
    }
}
