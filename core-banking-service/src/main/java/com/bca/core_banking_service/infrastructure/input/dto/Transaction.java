package com.bca.core_banking_service.infrastructure.input.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

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