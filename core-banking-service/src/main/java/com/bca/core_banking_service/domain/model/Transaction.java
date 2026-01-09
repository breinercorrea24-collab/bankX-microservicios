package com.bca.core_banking_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private String fromAccountId;
    private String toAccountId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balance;
    private LocalDateTime timestamp;

    public enum TransactionType {
        DEPOSIT, WITHDRAW, TRANSFER
    }
}