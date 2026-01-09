package com.bca.customers_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryResponse {
    private String customerId;
    private Integer totalAccounts;
    private Double totalBalance;
    private List<AccountSummary> accounts;
    private List<CreditSummary> credits;
}