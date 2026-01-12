package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;
import java.util.Currency;

public abstract class BankAccount {

    protected String id;
    protected String customerId;
    protected BigDecimal balance;
    protected Currency currency;

    protected int freeTransactionLimit;
    protected BigDecimal commissionPerExtra;

    public abstract void deposit(BigDecimal amount);
    public abstract void withdraw(BigDecimal amount);
}