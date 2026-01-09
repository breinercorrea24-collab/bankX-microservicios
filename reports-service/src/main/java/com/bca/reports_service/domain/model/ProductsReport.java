package com.bca.reports_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsReport {
    private Product product;
    private Period period;
    private Summary summary;
    private List<Transaction> transactions;
    private List<Commission> commissions;
    private Metrics metrics;

    public Product getProduct() {
        return product;
    }

    public Period getPeriod() {
        return period;
    }

    public Summary getSummary() {
        return summary;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Commission> getCommissions() {
        return commissions;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        private String productId;
        private String productType;
        private String name;
        private String currency;
    }

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
    public static class Summary {
        private Double openingBalance;
        private Double closingBalance;
        private Double totalCredits;
        private Double totalDebits;
        private Double totalCommissions;

        public Summary(Integer totalMovements, Double totalAmount, Double totalCommissions) {
            this.openingBalance = null;
            this.closingBalance = null;
            this.totalCredits = totalAmount;
            this.totalDebits = totalMovements.doubleValue();
            this.totalCommissions = totalCommissions;
        }

        public Double getTotalCredits() {
            return totalCredits;
        }

        public Double getTotalCommissions() {
            return totalCommissions;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transaction {
        private String transactionId;
        private String type;
        private Double amount;
        private LocalDate date;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Commission {
        private String commissionType;
        private Double amount;
        private LocalDate date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metrics {
        private Integer totalTransactions;
        private Double averageDailyBalance;
    }
}