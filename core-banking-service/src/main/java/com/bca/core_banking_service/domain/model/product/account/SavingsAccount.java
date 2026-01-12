package com.bca.core_banking_service.domain.model.product.account;

public class SavingsAccount extends Account {

    private int maxMonthlyTransactions;
    private int currentTransactions;

    @Override
    public boolean hasMaintenanceFee() {
        return false;
    }

    public void validateMonthlyLimit(){
        if(currentTransactions >= maxMonthlyTransactions){
            throw new RuntimeException("Límite mensual alcanzado");
        }
    }

    @Override
    public void validateCreation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCreation'");
    }
}
