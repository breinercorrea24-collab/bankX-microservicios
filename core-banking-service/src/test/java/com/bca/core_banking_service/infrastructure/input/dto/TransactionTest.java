package com.bca.core_banking_service.infrastructure.input.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void builder_setsAllFields() {
        LocalDateTime now = LocalDateTime.now();

        Transaction tx = Transaction.builder()
                .id("tx-1")
                .accountId("acc-1")
                .fromAccountId("from-acc")
                .toAccountId("to-acc")
                .type(Transaction.TransactionType.TRANSFER)
                .amount(BigDecimal.valueOf(25))
                .balance(BigDecimal.valueOf(100))
                .timestamp(now)
                .build();

        assertEquals("tx-1", tx.getId());
        assertEquals("acc-1", tx.getAccountId());
        assertEquals("from-acc", tx.getFromAccountId());
        assertEquals("to-acc", tx.getToAccountId());
        assertEquals(Transaction.TransactionType.TRANSFER, tx.getType());
        assertEquals(BigDecimal.valueOf(25), tx.getAmount());
        assertEquals(BigDecimal.valueOf(100), tx.getBalance());
        assertEquals(now, tx.getTimestamp());
    }

    @Test
    void noArgsConstructor_allowsSetterUsage() {
        Transaction tx = new Transaction();
        LocalDateTime ts = LocalDateTime.now();

        tx.setId("tx-2");
        tx.setAccountId("acc-2");
        tx.setFromAccountId(null);
        tx.setToAccountId(null);
        tx.setType(Transaction.TransactionType.DEPOSIT);
        tx.setAmount(BigDecimal.TEN);
        tx.setBalance(BigDecimal.ONE);
        tx.setTimestamp(ts);

        assertEquals("tx-2", tx.getId());
        assertEquals("acc-2", tx.getAccountId());
        assertNull(tx.getFromAccountId());
        assertNull(tx.getToAccountId());
        assertEquals(Transaction.TransactionType.DEPOSIT, tx.getType());
        assertEquals(BigDecimal.TEN, tx.getAmount());
        assertEquals(BigDecimal.ONE, tx.getBalance());
        assertEquals(ts, tx.getTimestamp());
    }

    @Test
    void builder_withoutSettingFields_defaultsToNulls() {
        Transaction tx = Transaction.builder().build();

        assertNull(tx.getId());
        assertNull(tx.getAccountId());
        assertNull(tx.getFromAccountId());
        assertNull(tx.getToAccountId());
        assertNull(tx.getType());
        assertNull(tx.getAmount());
        assertNull(tx.getBalance());
        assertNull(tx.getTimestamp());
    }

    @Test
    void allArgsConstructor_setsFields() {
        LocalDateTime now = LocalDateTime.now();
        Transaction tx = new Transaction(
                "tx-3",
                "acc-3",
                "from",
                "to",
                Transaction.TransactionType.WITHDRAW,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(95),
                now);

        assertEquals("tx-3", tx.getId());
        assertEquals("acc-3", tx.getAccountId());
        assertEquals("from", tx.getFromAccountId());
        assertEquals("to", tx.getToAccountId());
        assertEquals(Transaction.TransactionType.WITHDRAW, tx.getType());
        assertEquals(BigDecimal.valueOf(5), tx.getAmount());
        assertEquals(BigDecimal.valueOf(95), tx.getBalance());
        assertEquals(now, tx.getTimestamp());
    }

    @Test
    void transactionType_valueOfMatchesNames() {
        assertEquals(Transaction.TransactionType.DEPOSIT, Transaction.TransactionType.valueOf("DEPOSIT"));
        assertEquals(Transaction.TransactionType.WITHDRAW, Transaction.TransactionType.valueOf("WITHDRAW"));
        assertEquals(Transaction.TransactionType.TRANSFER, Transaction.TransactionType.valueOf("TRANSFER"));
        assertThrows(IllegalArgumentException.class, () -> Transaction.TransactionType.valueOf("UNKNOWN"));
    }

    @Test
    void equalsAndHashCode_areGeneratedByDataAnnotation() {
        LocalDateTime now = LocalDateTime.now();
        Transaction tx1 = new Transaction(
                "tx-4",
                "acc-4",
                null,
                null,
                Transaction.TransactionType.DEPOSIT,
                BigDecimal.ONE,
                BigDecimal.TEN,
                now);
        Transaction tx2 = new Transaction(
                "tx-4",
                "acc-4",
                null,
                null,
                Transaction.TransactionType.DEPOSIT,
                BigDecimal.ONE,
                BigDecimal.TEN,
                now);

        assertEquals(tx1, tx2);
        assertEquals(tx1.hashCode(), tx2.hashCode());
        // touch toString() as well
        assertTrue(tx1.toString().contains("tx-4"));
    }
}
