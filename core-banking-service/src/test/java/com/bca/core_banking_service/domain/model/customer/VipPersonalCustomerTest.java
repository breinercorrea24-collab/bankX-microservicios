package com.bca.core_banking_service.domain.model.customer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VipPersonalCustomerTest {

    @Test
    void vipPersonalCustomerAllowsAccountsAndCredits() {
        VipPersonalCustomer customer = new VipPersonalCustomer();

        assertTrue(customer.canCreateAccount());
        assertTrue(customer.canCreateCredit());
    }
}
