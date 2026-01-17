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
    void equalsIsReflexiveAndHandlesNullAndDifferentTypes() {
        TestProduct product = new TestProduct("prod-8", "cust-8", ProductStatus.ACTIVE);
        assertEquals(product, product);
        assertNotEquals(product, null);
        assertNotEquals(product, "unrelated");
    }

    @Test
    void equalsHandlesNullFieldValues() {
        TestProduct first = new TestProduct("prod-9", "cust-9", ProductStatus.ACTIVE);
        first.setId(null);
        first.setCustomerId(null);
        first.setStatus(null);
        first.setCreatedAt(java.time.LocalDateTime.of(2024, 1, 1, 0, 0));

        TestProduct second = new TestProduct("prod-10", "cust-10", ProductStatus.CLOSED);
        second.setId(null);
        second.setCustomerId(null);
        second.setStatus(null);
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setId("different");
        assertNotEquals(first, second);
    }

    @Test
    void equalsDetectsDifferentCreatedAtValues() {
        TestProduct first = new TestProduct("prod-11", "cust-11", ProductStatus.ACTIVE);
        TestProduct second = new TestProduct("prod-11", "cust-11", ProductStatus.ACTIVE);
        second.setCreatedAt(first.getCreatedAt().minusSeconds(5));

        assertNotEquals(first, second);
    }

    @Test
    void equalsRespectsCustomCanEqualImplementation() {
        TestProduct first = new TestProduct("prod-12", "cust-12", ProductStatus.ACTIVE);
        NonComparableProduct other = new NonComparableProduct("prod-12", "cust-12", ProductStatus.ACTIVE);
        other.setCreatedAt(first.getCreatedAt());

        assertNotEquals(first, other);
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

    private static class NonComparableProduct extends Product {
        NonComparableProduct(String id, String customerId, ProductStatus status) {
            super(id, customerId, status);
        }

        @Override
        public void validateCreation() {
            // no-op
        }

        @Override
        public boolean canEqual(Object other) {
            return other instanceof NonComparableProduct;
        }
    }
}
