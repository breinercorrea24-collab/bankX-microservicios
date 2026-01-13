package com.bca.core_banking_service.domain.model.product.credit;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

public class PersonalCredit extends Credit {

    public PersonalCredit(String id, String customerId, ProductStatus status) {
        super(id, customerId, status);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void validateCreation() {
        // validar que no tenga otro crédito
    }
}
