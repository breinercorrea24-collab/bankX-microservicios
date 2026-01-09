package com.bca.customers_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditSummary {
    private String creditId;
    private Double totalAmount;
    private Double pendingDebt;
    private String currency;
    private CreditStatus status;

    public enum CreditStatus {
        ACTIVE, CANCELLED
    }
}