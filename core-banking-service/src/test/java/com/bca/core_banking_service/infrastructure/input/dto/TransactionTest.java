package com.bca.core_banking_service.infrastructure.input.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void transactionType_valueOfMatchesNames() {
        assertEquals(Transaction.TransactionType.DEPOSIT, Transaction.TransactionType.valueOf("DEPOSIT"));
        assertEquals(Transaction.TransactionType.WITHDRAW, Transaction.TransactionType.valueOf("WITHDRAW"));
        assertEquals(Transaction.TransactionType.TRANSFER, Transaction.TransactionType.valueOf("TRANSFER"));
        assertThrows(IllegalArgumentException.class, () -> Transaction.TransactionType.valueOf("UNKNOWN"));
    }
}
