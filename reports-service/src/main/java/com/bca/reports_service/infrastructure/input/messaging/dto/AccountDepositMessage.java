package com.bca.reports_service.infrastructure.input.messaging.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDepositMessage {

    private String accountId;
    private BigDecimal amount;
    private BigDecimal balance;
}