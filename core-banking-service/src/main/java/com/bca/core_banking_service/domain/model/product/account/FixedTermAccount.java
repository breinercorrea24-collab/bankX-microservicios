package com.bca.core_banking_service.domain.model.product.account;

import java.time.LocalDate;

public class FixedTermAccount extends Account {

    private int allowedDay;
    private LocalDate lastOperationDate;

    @Override
    public boolean hasMaintenanceFee() {
        return false;
    }

    public void validateOperationDate(LocalDate date){
        if(date.getDayOfMonth() != allowedDay){
            throw new RuntimeException("Operación no permitida hoy");
        }
    }

    @Override
    public void validateCreation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCreation'");
    }
}
