package com.bca.core_banking_service.domain.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class ProductTest {

    @Test
    void constructorInitializesFields() {
        TestProduct product = new TestProduct("prod-1", "cust-1", ProductStatus.ACTIVE);

        assertEquals("prod-1", product.getId());
        assertEquals("cust-1", product.getCustomerId());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
        assertNotNull(product.getCreatedAt());
        assertTrue(product.isActive());
    }

    @Test
    void isActiveReturnsFalseWhenStatusNotActive() {
        TestProduct product = new TestProduct("prod-2", "cust-2", ProductStatus.CLOSED);

        assertFalse(product.isActive());
    }

    private static class TestProduct extends Product {
        TestProduct(String id, String customerId, ProductStatus status) {
            super(id, customerId, status);
        }

        @Override
        public void validateCreation() {
            // no-op for tests
        }
    }
}
