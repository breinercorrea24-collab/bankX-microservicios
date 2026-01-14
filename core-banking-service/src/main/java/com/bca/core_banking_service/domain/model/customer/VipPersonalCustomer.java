package com.bca.core_banking_service.domain.model.customer;

public class VipPersonalCustomer extends PersonalCustomer {

    @Override
    public boolean canCreateAccount() {
        return true;
    }

    @Override
    public boolean canCreateCredit() {
        return true; // múltiples créditos
    }
    
}
