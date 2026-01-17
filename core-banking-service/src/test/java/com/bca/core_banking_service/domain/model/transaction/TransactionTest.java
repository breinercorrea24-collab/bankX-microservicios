package com.bca.core_banking_service.domain.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.transaction.Channel;
import com.bca.core_banking_service.domain.model.enums.transaction.TransactionType;

class TransactionTest {

    @Test
    void builderAndGetters_workAsExpected() {
        LocalDateTime date = LocalDateTime.now();

        Transaction transaction = Transaction.builder()
                .id("tx-1")
                .productId("prod-1")
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .commission(BigDecimal.valueOf(2))
                .date(date)
                .channel(Channel.MOBILE)
                .build();

        assertEquals("tx-1", transaction.getId());
        assertEquals("prod-1", transaction.getProductId());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(BigDecimal.valueOf(100), transaction.getAmount());
        assertEquals(BigDecimal.valueOf(2), transaction.getCommission());
        assertEquals(date, transaction.getDate());
        assertEquals(Channel.MOBILE, transaction.getChannel());
    }

    @Test
    void setters_allowMutatingTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId("tx-2");
        transaction.setProductId("prod-2");
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setCommission(BigDecimal.ONE);
        transaction.setDate(LocalDateTime.of(2024, 5, 1, 10, 0));
        transaction.setChannel(Channel.MOBILE);

        assertEquals("tx-2", transaction.getId());
        assertEquals("prod-2", transaction.getProductId());
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(BigDecimal.TEN, transaction.getAmount());
        assertEquals(BigDecimal.ONE, transaction.getCommission());
        assertEquals(LocalDateTime.of(2024, 5, 1, 10, 0), transaction.getDate());
        assertEquals(Channel.MOBILE, transaction.getChannel());
    }
}
