package com.bca.reports_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class DailyAverageBalanceReport {
    private String customerId;
    private Period period;
    private List<ProductBalance> productBalances;
    private Totals totals;

    // Legacy fields for backwards compatibility
    private Double averageBalance;
    private List<DailyBalance> dailyBalances;

    // Constructor for new structure (without legacy fields)
    public DailyAverageBalanceReport(String customerId, Period period, List<ProductBalance> productBalances, Totals totals) {
        this.customerId = customerId;
        this.period = period;
        this.productBalances = productBalances;
        this.totals = totals;
        this.averageBalance = null;
        this.dailyBalances = null;
    }

    // Full constructor for complete initialization
    @AllArgsConstructor
    public static class FullConstructor {
        public DailyAverageBalanceReport create(String customerId, Period period, List<ProductBalance> productBalances, 
                                              Totals totals, Double averageBalance, List<DailyBalance> dailyBalances) {
            DailyAverageBalanceReport report = new DailyAverageBalanceReport(customerId, period, productBalances, totals);
            report.setAverageBalance(averageBalance);
            report.setDailyBalances(dailyBalances);
            return report;
        }
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
    public static class DailyBalance {
        private LocalDate date;
        private Double balance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Totals {
        private Double averageAssets;
        private Double averageLiabilities;
        private Double netAveragePosition;

        public Double getAverageAssets() {
            return averageAssets;
        }

        public Double getAverageLiabilities() {
            return averageLiabilities;
        }

        public Double getNetAveragePosition() {
            return netAveragePosition;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductBalance {
        private String productType;
        private String productId;
        private Double averageDailyBalance;
        private String currency;
        private String status;

        public String getProductType() {
            return productType;
        }

        public String getProductId() {
            return productId;
        }

        public Double getAverageDailyBalance() {
            return averageDailyBalance;
        }

        public String getCurrency() {
            return currency;
        }

        public String getStatus() {
            return status;
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public Period getPeriod() {
        return period;
    }

    public List<ProductBalance> getProductBalances() {
        return productBalances;
    }

    public Totals getTotals() {
        return totals;
    }
}