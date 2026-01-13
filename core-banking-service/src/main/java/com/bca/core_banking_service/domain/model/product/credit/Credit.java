package com.bca.core_banking_service.domain.model.product.credit;

import java.math.BigDecimal;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.Product;

public abstract class Credit extends Product {

    public Credit(String id, String customerId, ProductStatus status) {
        super(id, customerId, status);
        //TODO Auto-generated constructor stub
    }

    protected BigDecimal creditLimit;
    protected BigDecimal outstandingBalance;
    protected double interestRate;

    public void pay(BigDecimal amount){
        if(amount.compareTo(outstandingBalance) > 0){
            throw new RuntimeException("Pago excede deuda");
        }
        outstandingBalance = outstandingBalance.subtract(amount);
    }

    public BigDecimal getAvailableCredit(){
        return creditLimit.subtract(outstandingBalance);
    }
}
