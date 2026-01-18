package com.bca.core_banking_service.domain.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @Test
    void setIdAllowsUpdatingIdentifier() {
        TestProduct product = new TestProduct("prod-3", "cust-3", ProductStatus.ACTIVE);
        product.setId("prod-4");
        assertEquals("prod-4", product.getId());
    }

    @Test
    void equalsAndHashCodeIncludeFields() {
        TestProduct first = new TestProduct("prod-5", "cust-5", ProductStatus.ACTIVE);
        TestProduct second = new TestProduct("prod-5", "cust-5", ProductStatus.ACTIVE);
        TestProduct different = new TestProduct("prod-6", "cust-6", ProductStatus.BLOCKED);

        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    void toStringContainsKeyInformation() {
        TestProduct product = new TestProduct("prod-7", "cust-7", ProductStatus.ACTIVE);
        String toString = product.toString();
        assertTrue(toString.contains("prod-7"));
        assertTrue(toString.contains("cust-7"));
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
