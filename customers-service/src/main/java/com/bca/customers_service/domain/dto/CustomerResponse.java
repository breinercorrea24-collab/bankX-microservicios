package com.bca.customers_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private String id;
    private String fullName;
    private String document;
    private CustomerType customerType;
    private KycLevel kycLevel;
    private CustomerStatus status;
    private LocalDateTime createdAt;

    public enum CustomerType {
        YANKI, BANKX
    }

    public enum KycLevel {
        LIGHT, FULL
    }

    public enum CustomerStatus {
        ACTIVE, PENDING_VERIFICATION, BLOCKED
    }
}