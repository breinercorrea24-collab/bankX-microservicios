package com.bca.core_banking_service.infrastructure.output.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TransactionEntityTest {

    private static final LocalDateTime FIXED_TIMESTAMP = LocalDateTime.of(2024, 1, 15, 10, 0, 0);

    @Test
    void allArgsConstructorPopulatesFields() {
        LocalDateTime timestamp = LocalDateTime.now();
        TransactionEntity entity = new TransactionEntity(
                "tx-1",
                "account-1",
                "from-acc",
                "to-acc",
                TransactionEntity.TransactionType.DEPOSIT,
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(1200),
                timestamp);

        assertEquals("tx-1", entity.getId());
        assertEquals("account-1", entity.getAccountId());
        assertEquals("from-acc", entity.getFromAccountId());
        assertEquals("to-acc", entity.getToAccountId());
        assertEquals(TransactionEntity.TransactionType.DEPOSIT, entity.getType());
        assertEquals(BigDecimal.valueOf(200), entity.getAmount());
        assertEquals(BigDecimal.valueOf(1200), entity.getBalance());
        assertEquals(timestamp, entity.getTimestamp());
    }

    @Test
    void settersAllowMutation() {
        TransactionEntity entity = new TransactionEntity();
        LocalDateTime timestamp = LocalDateTime.now();
        entity.setId("tx-2");
        entity.setAccountId("account-2");
        entity.setFromAccountId("from");
        entity.setToAccountId("to");
        entity.setType(TransactionEntity.TransactionType.WITHDRAW);
        entity.setAmount(BigDecimal.valueOf(50));
        entity.setBalance(BigDecimal.valueOf(950));
        entity.setTimestamp(timestamp);

        assertEquals("tx-2", entity.getId());
        assertEquals("account-2", entity.getAccountId());
        assertEquals("from", entity.getFromAccountId());
        assertEquals("to", entity.getToAccountId());
        assertEquals(TransactionEntity.TransactionType.WITHDRAW, entity.getType());
        assertEquals(BigDecimal.valueOf(50), entity.getAmount());
        assertEquals(BigDecimal.valueOf(950), entity.getBalance());
        assertEquals(timestamp, entity.getTimestamp());
    }

    @Test
    void equalsAndHashCodeIncludeFields() {
        TransactionEntity first = sampleEntity("tx-3");
        TransactionEntity second = sampleEntity("tx-3");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        TransactionEntity different = sampleEntity("tx-4");
        assertNotEquals(first, different);
    }

    @Test
    void toStringContainsKeyInformation() {
        TransactionEntity entity = sampleEntity("tx-5");
        String description = entity.toString();
        assertTrue(description.contains("tx-5"));
        assertTrue(description.contains("account-5"));
    }

    private TransactionEntity sampleEntity(String id) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(id);
        entity.setAccountId("account-5");
        entity.setFromAccountId("from");
        entity.setToAccountId("to");
        entity.setType(TransactionEntity.TransactionType.TRANSFER);
        entity.setAmount(BigDecimal.TEN);
        entity.setBalance(BigDecimal.valueOf(100));
        entity.setTimestamp(FIXED_TIMESTAMP);
        return entity;
    }
}
