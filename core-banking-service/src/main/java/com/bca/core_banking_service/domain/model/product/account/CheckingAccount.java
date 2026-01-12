package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;

public class CheckingAccount extends BankAccount {

    private BigDecimal maintenanceCommission;

    public void chargeMaintenanceFee(){
        this.balance = this.balance.subtract(maintenanceCommission);
    }

    @Override
    public void deposit(BigDecimal amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deposit'");
    }

    @Override
    public void withdraw(BigDecimal amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdraw'");
    }
}