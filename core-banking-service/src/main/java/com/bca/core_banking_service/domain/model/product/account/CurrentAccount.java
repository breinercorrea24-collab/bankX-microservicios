package com.bca.core_banking_service.domain.model.product.account;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

public class CurrentAccount extends Account {

    public CurrentAccount(String customerId, String currency, ProductStatus status) {
        super(customerId, currency, status, BigDecimal.ZERO);
        //TODO Auto-generated constructor stub
    }

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
