package com.bca.core_banking_service.infrastructure.output.persistence.mapper;


import com.bca.core_banking_service.infrastructure.input.dto.Transaction;
import com.bca.core_banking_service.infrastructure.input.dto.Transaction.TransactionType;
import com.bca.core_banking_service.infrastructure.output.persistence.entity.TransactionEntity;

public class TransactionMapper {

    private TransactionMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Transaction toDomain(TransactionEntity e) {
        return new Transaction(
            e.getId(),
            e.getAccountId(),
            e.getFromAccountId(),
            e.getToAccountId(),
            TransactionType.valueOf(e.getType().name()),
            e.getAmount(),
            e.getBalance(),
            e.getTimestamp()
        );
    }

    public static TransactionEntity toEntity(Transaction d) {
        TransactionEntity e = new TransactionEntity();
        e.setId(d.getId());
        e.setAccountId(d.getAccountId());
        e.setFromAccountId(d.getFromAccountId());
        e.setToAccountId(d.getToAccountId());
        e.setType(TransactionEntity.TransactionType.valueOf(d.getType().name()));
        e.setAmount(d.getAmount());
        e.setBalance(d.getBalance());
        e.setTimestamp(d.getTimestamp());
        return e;
    }
}
