package com.bca.core_banking_service.domain.model.product;

import java.time.LocalDateTime;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

public abstract class Product {

    protected String id;
    protected String customerId;
    protected ProductStatus status;
    protected LocalDateTime createdAt;

    public abstract void validateCreation();

    public boolean isActive() {
        return status == ProductStatus.ACTIVE;
    }
}
