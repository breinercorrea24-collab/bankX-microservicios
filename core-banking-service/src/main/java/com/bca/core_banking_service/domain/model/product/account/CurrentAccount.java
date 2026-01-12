package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;

public class CurrentAccount extends Account {

    private BigDecimal maintenanceFee;

    @Override
    public boolean hasMaintenanceFee() {
        return true;
    }

    public BigDecimal getMaintenanceFee(){
        return maintenanceFee;
    }

    @Override
    public void validateCreation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCreation'");
    }
}
