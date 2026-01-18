package com.bca.core_banking_service.infrastructure.output.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.infrastructure.input.dto.Transaction;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.TransactionEntity;

class TransactionMapperTest {

    @Test
    void toDomain_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        TransactionEntity entity = new TransactionEntity(
                "tx-1",
                "acc-1",
                null,
                null,
                TransactionEntity.TransactionType.DEPOSIT,
                BigDecimal.TEN,
                BigDecimal.valueOf(50),
                now);

        Transaction domain = TransactionMapper.toDomain(entity);

        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getAccountId(), domain.getAccountId());
        assertEquals(entity.getType().name(), domain.getType().name());
        assertEquals(entity.getAmount(), domain.getAmount());
        assertEquals(entity.getBalance(), domain.getBalance());
        assertEquals(entity.getTimestamp(), domain.getTimestamp());
    }

    @Test
    void toEntity_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Transaction domain = new Transaction(
                "tx-2",
                "acc-2",
                "from-acc",
                "to-acc",
                Transaction.TransactionType.TRANSFER,
                BigDecimal.ONE,
                BigDecimal.valueOf(200),
                now);

        TransactionEntity entity = TransactionMapper.toEntity(domain);

        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getAccountId(), entity.getAccountId());
        assertEquals(domain.getFromAccountId(), entity.getFromAccountId());
        assertEquals(domain.getToAccountId(), entity.getToAccountId());
        assertEquals(domain.getType().name(), entity.getType().name());
        assertEquals(domain.getAmount(), entity.getAmount());
        assertEquals(domain.getBalance(), entity.getBalance());
        assertEquals(domain.getTimestamp(), entity.getTimestamp());
    }

    @Test
    void constructor_isInaccessible() {
        assertThrows(IllegalStateException.class, () -> {
            var ctor = TransactionMapper.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            ctor.newInstance();
        });
    }
}
