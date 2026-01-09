package com.bca.customers_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummary {
    private String accountId;
    private AccountType accountType;
    private String currency;
    private Double balance;
    private Double availableBalance;
    private AccountStatus status;

    public enum AccountType {
        SAVINGS, CURRENT
    }

    public enum AccountStatus {
        ACTIVE, BLOCKED
    }
}