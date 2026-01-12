package com.bca.core_banking_service.domain.model.customer;

import com.bca.core_banking_service.domain.model.enums.account.CustomerSegment;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;

public abstract class Customer {

    protected String id;
    protected String name;
    protected String documentNumber;
    protected CustomerType type;
    protected CustomerSegment segment;

    public abstract boolean canCreateAccount();
    public abstract boolean canCreateCredit();

    // getters
}