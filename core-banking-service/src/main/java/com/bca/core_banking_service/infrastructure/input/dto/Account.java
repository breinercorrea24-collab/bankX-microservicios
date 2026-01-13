package com.bca.core_banking_service.infrastructure.input.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private String id;
    private String customerId;
    private AccountType type;
    protected String accountNumber;
    private String currency;
    private BigDecimal balance;
    private AccountStatus status;
    private boolean isActive;

    public enum AccountType {
        SAVINGS, CURRENT, CHECKING, FIXED_TERM
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE
    }

    public void deposit(BigDecimal amount){
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount){
        this.balance = this.balance.subtract(amount);
    }
}