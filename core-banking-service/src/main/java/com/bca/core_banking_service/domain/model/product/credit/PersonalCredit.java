package com.bca.core_banking_service.domain.model.product.credit;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

public class PersonalCredit extends Credit {

    public PersonalCredit(String id, String customerId, ProductStatus status) {
        super(id, customerId, status);
    }
}
