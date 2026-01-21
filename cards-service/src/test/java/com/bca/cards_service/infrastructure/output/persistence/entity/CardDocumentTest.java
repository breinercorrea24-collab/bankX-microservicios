package com.bca.cards_service.infrastructure.output.persistence.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardDocumentTest {

    @Test
    void storesAndExposesAllFields() {
        LocalDateTime now = LocalDateTime.now();
        CardDocument doc = new CardDocument(
                "mongo-id",
                "card-1",
                "CREDIT",
                "cust-1",
                "****1234",
                "ACTIVE",
                new BigDecimal("5000"),
                new BigDecimal("4500"),
                "acc-1",
                now);

        assertEquals("mongo-id", doc.id());
        assertEquals("card-1", doc.cardId());
        assertEquals("CREDIT", doc.type());
        assertEquals("cust-1", doc.customerId());
        assertEquals("****1234", doc.maskedNumber());
        assertEquals("ACTIVE", doc.status());
        assertEquals(new BigDecimal("5000"), doc.creditLimit());
        assertEquals(new BigDecimal("4500"), doc.availableLimit());
        assertEquals("acc-1", doc.linkedAccountId());
        assertEquals(now, doc.createdAt());
    }

    @Test
    void implementsRecordEqualityAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        CardDocument base = new CardDocument(
                "id-a", "card-a", "DEBIT", "cust-a", "****0000", "ACTIVE",
                BigDecimal.ONE, BigDecimal.ONE, "acc-a", now);
        CardDocument same = new CardDocument(
                "id-a", "card-a", "DEBIT", "cust-a", "****0000", "ACTIVE",
                BigDecimal.ONE, BigDecimal.ONE, "acc-a", now);
        CardDocument different = new CardDocument(
                "id-b", "card-b", "DEBIT", "cust-b", "****1111", "BLOCKED",
                BigDecimal.TEN, BigDecimal.TEN, "acc-b", now);

        assertEquals(base, same);
        assertEquals(base.hashCode(), same.hashCode());
        assertNotEquals(base, different);
    }

    @Test
    void toStringContainsKeyFields() {
        CardDocument doc = new CardDocument(
                "id-toString", "card-ts", "DEBIT", "cust-ts", "****9999", "ACTIVE",
                BigDecimal.ZERO, BigDecimal.ZERO, "acc-ts", LocalDateTime.MIN);

        String text = doc.toString();
        assertTrue(text.contains("id-toString"));
        assertTrue(text.contains("card-ts"));
        assertTrue(text.contains("cust-ts"));
    }
}
