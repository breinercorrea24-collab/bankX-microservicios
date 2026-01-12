package com.bca.core_banking_service.domain.model.customer;

public class BusinessCustomer extends Customer {

    private String companyName;
    private String ruc;

    @Override
    public boolean canCreateAccount() {
        return true;
    }

    @Override
    public boolean canCreateCredit() {
        return true; // múltiples créditos
    }
}
