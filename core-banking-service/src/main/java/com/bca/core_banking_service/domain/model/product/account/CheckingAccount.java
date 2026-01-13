package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;

public class CheckingAccount extends Account {

    private BigDecimal maintenanceCommission;
    private int maxMonthlyTransactions;

    public CheckingAccount(String customerId, String currency, int maxMonthlyTransactions, BigDecimal maintenanceCommission,
            BigDecimal initialDeposit) {
        /* super(customerId, currency, maxMonthlyTransactions); */
        this.maxMonthlyTransactions = maxMonthlyTransactions;
        this.maintenanceCommission = maintenanceCommission;
        this.balance = initialDeposit;
    }

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

    @Override
    public boolean hasMaintenanceFee() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasMaintenanceFee'");
    }

    @Override
    public void validateCreation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCreation'");
    }
}