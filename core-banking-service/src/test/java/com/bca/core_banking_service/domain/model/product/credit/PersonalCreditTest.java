package com.bca.core_banking_service.domain.model.product.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class PersonalCreditTest {

    @Test
    void constructorInitializesFields() {
        PersonalCredit credit = new PersonalCredit("cred-2", "customer-2", ProductStatus.ACTIVE);

        assertEquals("cred-2", credit.getId());
        assertEquals("customer-2", credit.getCustomerId());
        assertEquals(ProductStatus.ACTIVE, credit.getStatus());
    }
}
