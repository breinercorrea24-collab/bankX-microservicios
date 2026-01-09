package com.bca.reports_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionReport {
    private String productType;
    private String productId;
    private Period period;
    private Double totalCommissionAmount;
    private String currency;
    private List<CommissionDetail> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Period {
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommissionDetail {
        private String transactionId;
        private String productId;
        private String commissionType;
        private Double amount;
        private LocalDate date;
    }
}