package com.bca.core_banking_service.infrastructure.input.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TransactionDtoTest {

    @Test
    void builderCreatesTransactionWithAllFields() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 2, 1, 12, 0);
        Transaction transaction = Transaction.builder()
                .id("tx-1")
                .accountId("account-1")
                .fromAccountId("from-acc")
                .toAccountId("to-acc")
                .type(Transaction.TransactionType.TRANSFER)
                .amount(BigDecimal.valueOf(75))
                .balance(BigDecimal.valueOf(500))
                .timestamp(timestamp)
                .build();

        assertEquals("tx-1", transaction.getId());
        assertEquals("account-1", transaction.getAccountId());
        assertEquals("from-acc", transaction.getFromAccountId());
        assertEquals("to-acc", transaction.getToAccountId());
        assertEquals(Transaction.TransactionType.TRANSFER, transaction.getType());
        assertEquals(BigDecimal.valueOf(75), transaction.getAmount());
        assertEquals(BigDecimal.valueOf(500), transaction.getBalance());
        assertEquals(timestamp, transaction.getTimestamp());
    }

    @Test
    void equalsAndHashCodeConsiderFields() {
        Transaction first = sampleTransaction("tx-2");
        Transaction second = sampleTransaction("tx-2");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setAmount(BigDecimal.valueOf(200));
        assertNotEquals(first, second);
    }

    @Test
    void settersWorkWithNoArgsConstructor() {
        Transaction transaction = new Transaction();
        LocalDateTime timestamp = LocalDateTime.now();
        transaction.setId("tx-3");
        transaction.setAccountId("account-3");
        transaction.setFromAccountId("from");
        transaction.setToAccountId("to");
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.ONE);
        transaction.setBalance(BigDecimal.TEN);
        transaction.setTimestamp(timestamp);

        assertEquals("tx-3", transaction.getId());
        assertEquals("account-3", transaction.getAccountId());
        assertEquals(Transaction.TransactionType.DEPOSIT, transaction.getType());
        assertEquals(BigDecimal.ONE, transaction.getAmount());
        assertEquals(BigDecimal.TEN, transaction.getBalance());
        assertEquals(timestamp, transaction.getTimestamp());
    }

    @Test
    void toStringIncludesKeyInformation() {
        Transaction transaction = sampleTransaction("tx-4");
        String description = transaction.toString();
        assertTrue(description.contains("tx-4"));
        assertTrue(description.contains("account-5"));
    }

    private Transaction sampleTransaction(String id) {
        return Transaction.builder()
                .id(id)
                .accountId("account-5")
                .fromAccountId("from")
                .toAccountId("to")
                .type(Transaction.TransactionType.WITHDRAW)
                .amount(BigDecimal.valueOf(10))
                .balance(BigDecimal.valueOf(90))
                .timestamp(LocalDateTime.of(2024, 3, 1, 9, 0))
                .build();
    }
}
