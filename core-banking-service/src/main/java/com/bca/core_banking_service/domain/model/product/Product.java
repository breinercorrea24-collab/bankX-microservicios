package com.bca.core_banking_service.domain.model.product;

import java.time.LocalDateTime;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

import lombok.Data;

@Data
public abstract class Product {

    protected String id;
    protected String customerId;
    protected ProductStatus status;
    protected LocalDateTime createdAt;


    public Product(String id, String customerId, ProductStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public abstract void validateCreation();

    public boolean isActive() {
        return status == ProductStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }
}
