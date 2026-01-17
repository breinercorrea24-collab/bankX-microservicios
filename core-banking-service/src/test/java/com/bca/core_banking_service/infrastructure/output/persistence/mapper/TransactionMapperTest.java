package com.bca.core_banking_service.infrastructure.output.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.infrastructure.input.dto.Transaction;
import com.bca.core_banking_service.infrastructure.input.dto.Transaction.TransactionType;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.TransactionEntity;

class TransactionMapperTest {

    @Test
    void toDomain_mapsEntityToDto() {
        LocalDateTime timestamp = LocalDateTime.now();
        TransactionEntity entity = new TransactionEntity(
                "tx-1",
                "acc-1",
                "acc-1",
                "acc-2",
                TransactionEntity.TransactionType.TRANSFER,
                BigDecimal.TEN,
                BigDecimal.valueOf(100),
                timestamp);

        Transaction transaction = TransactionMapper.toDomain(entity);

        assertEquals("tx-1", transaction.getId());
        assertEquals("acc-1", transaction.getAccountId());
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(BigDecimal.TEN, transaction.getAmount());
        assertEquals(timestamp, transaction.getTimestamp());
    }

    @Test
    void toEntity_mapsDtoToEntity() {
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction dto = new Transaction(
                "tx-2",
                "acc-3",
                "acc-3",
                "acc-4",
                TransactionType.DEPOSIT,
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(200),
                timestamp);

        TransactionEntity entity = TransactionMapper.toEntity(dto);

        assertEquals("tx-2", entity.getId());
        assertEquals("acc-3", entity.getAccountId());
        assertEquals(TransactionEntity.TransactionType.DEPOSIT, entity.getType());
        assertEquals(BigDecimal.valueOf(50), entity.getAmount());
        assertEquals(timestamp, entity.getTimestamp());
    }
}
