package com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDepositEvent {

    public String accountId;
    public BigDecimal amount;
    public BigDecimal newBalance;
    public LocalDateTime occurredAt;

    public AccountDepositEvent(
        String accountId,
        BigDecimal amount,
        BigDecimal newBalance
    ){
        this.accountId = accountId;
        this.amount = amount;
        this.newBalance = newBalance;
        this.occurredAt = LocalDateTime.now();
    }
}
