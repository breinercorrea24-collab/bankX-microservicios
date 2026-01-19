package com.bca.core_banking_service.domain.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class ProductTest {

    @Test
    void productConstructor_setsFieldsAndIsActiveReflectsStatus() {
        TestProduct product = new TestProduct("prod-1", "cust-1", ProductStatus.ACTIVE);

        assertEquals("prod-1", product.getId());
        assertEquals("cust-1", product.getCustomerId());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
        assertNotNull(product.getCreatedAt());
        assertTrue(product.isActive());
    }

    private static class TestProduct extends Product {
        TestProduct(String id, String customerId, ProductStatus status) {
            super(id, customerId, status);
        }

        @Override
        public void validateCreation() {
            // no-op for test
        }
    }
}
