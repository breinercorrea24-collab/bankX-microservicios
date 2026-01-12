package com.bca.core_banking_service.domain.model.customer;

public class PersonalCustomer extends Customer {

    private String birthDate;

    @Override
    public boolean canCreateAccount() {
        return true;
    }

    @Override
    public boolean canCreateCredit() {
        return true; // solo 1 crédito
    }
}
