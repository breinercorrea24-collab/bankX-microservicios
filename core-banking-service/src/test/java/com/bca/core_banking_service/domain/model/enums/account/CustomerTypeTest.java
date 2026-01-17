package com.bca.core_banking_service.domain.model.enums.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CustomerTypeTest {

    @Test
    void enumsExposeAllCustomerTypes() {
        assertEquals(CustomerType.PERSONAL, CustomerType.valueOf("PERSONAL"));
        assertEquals(CustomerType.BUSINESS, CustomerType.values()[1]);
        assertEquals(CustomerType.VIPPERSONAL, CustomerType.values()[2]);
        assertEquals(CustomerType.PYMEBUSINESS, CustomerType.values()[3]);
    }
}
