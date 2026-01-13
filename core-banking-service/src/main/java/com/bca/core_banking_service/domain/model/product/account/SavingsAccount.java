package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;


public class SavingsAccount extends Account {

    private int maxMonthlyTransactions;
    private int currentTransactions;
    private BigDecimal maintenanceCommission;

    public SavingsAccount(String customerId, String currency, int maxMonthlyTransactions, BigDecimal maintenanceCommission,
            BigDecimal initialDeposit) {
        // super(customerId, currency, 0.0);
        this.maxMonthlyTransactions = maxMonthlyTransactions;
        this.maintenanceCommission = maintenanceCommission;
    }

    @Override
    public boolean hasMaintenanceFee() {
        return false;
    }

    public void validateMonthlyLimit(){
        if(currentTransactions >= maxMonthlyTransactions){
            throw new RuntimeException("Límite mensual alcanzado");
        }
    }

    public int getMaxMonthlyTransactions() {
        return maxMonthlyTransactions;
    }

    public void setMaxMonthlyTransactions(int maxMonthlyTransactions) {
        this.maxMonthlyTransactions = maxMonthlyTransactions;
    }

    public BigDecimal getMaintenanceCommission() {
        return maintenanceCommission;
    }

    public void setMaintenanceCommission(BigDecimal maintenanceCommission) {
        this.maintenanceCommission = maintenanceCommission;
    }

    @Override
    public void validateCreation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCreation'");
    }
}
