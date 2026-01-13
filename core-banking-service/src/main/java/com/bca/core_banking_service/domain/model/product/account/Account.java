package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.product.Product;
import com.bca.core_banking_service.infrastructure.input.dto.Account.AccountType;

import lombok.Data;

@Data
public abstract class Account extends Product {

    protected String accountNumber;
    protected BigDecimal balance;
    protected String currency;
    protected AccountType type;

    protected String id;
    protected String customerId;

    protected int freeTransactionLimit;
    protected BigDecimal commissionPerExtra;

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);
        if(balance.compareTo(amount) < 0){
            throw new RuntimeException("Saldo insuficiente");
        }
        balance = balance.subtract(amount);
    }

    protected void validateAmount(BigDecimal amount){
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Monto inválido");
        }
    }

    public abstract boolean hasMaintenanceFee();
}
