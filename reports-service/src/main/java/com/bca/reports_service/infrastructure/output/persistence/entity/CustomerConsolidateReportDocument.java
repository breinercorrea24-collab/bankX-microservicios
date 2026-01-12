package com.bca.reports_service.infrastructure.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "customer_consolidate_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerConsolidateReportDocument {
    @Id
    private String id;
    private String customerId;
    private LocalDateTime generatedAt;
    private Customer customer;
    private List<Account> accounts;
    private List<Card> cards;
    private List<Credit> credits;
    private List<Wallet> wallets;
    private Totals totals;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customer {
        private String name;
        private String document;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Account {
        private String accountId;
        private String type;
        private String currency;
        private Double balance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        private String cardId;
        private String type;
        private String maskedNumber;
        private String status;
        private Double availableLimit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Credit {
        private String creditId;
        private Double originalAmount;
        private Double pendingDebt;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Wallet {
        private String walletId;
        private String type;
        private Double balance;
        private String currency;
        private Double balanceBTC;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Totals {
        private Double totalBalancePEN;
        private Double totalDebt;
    }
}
