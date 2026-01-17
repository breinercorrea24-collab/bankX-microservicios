package com.bca.core_banking_service.domain.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bca.core_banking_service.domain.model.enums.transaction.Channel;
import com.bca.core_banking_service.domain.model.enums.transaction.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String id;
    private String productId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal commission;
    private LocalDateTime date;
    private Channel channel;
}
