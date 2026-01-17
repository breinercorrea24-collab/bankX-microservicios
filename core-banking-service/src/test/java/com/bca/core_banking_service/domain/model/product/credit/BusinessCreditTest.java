package com.bca.core_banking_service.domain.model.product.credit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class BusinessCreditTest {

    @Test
    void constructorInitializesFields() {
        BusinessCredit credit = new BusinessCredit("cred-1", "customer-1", ProductStatus.ACTIVE);

        assertEquals("cred-1", credit.getId());
        assertEquals("customer-1", credit.getCustomerId());
        assertEquals(ProductStatus.ACTIVE, credit.getStatus());
        assertTrue(credit.isActive());
    }

    @Test
    void validateCreation_allowsMultipleCredits() {
        BusinessCredit credit = new BusinessCredit("cred-1", "customer-1", ProductStatus.ACTIVE);

        assertDoesNotThrow(credit::validateCreation);
    }
}
