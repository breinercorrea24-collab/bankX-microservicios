package com.bca.core_banking_service.domain.model.product.credit;

public class PersonalCredit extends Credit {

    @Override
    public void validateCreation() {
        // validar que no tenga otro crédito
    }
}
