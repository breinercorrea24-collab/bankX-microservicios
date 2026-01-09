package com.bca.reports_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerConsolidateReport {
    private String customerId;
    private LocalDateTime generatedAt;
    private Customer customer;
    private List<Account> accounts;
    private List<Card> cards;
    private List<Credit> credits;
    private Map<String, Wallet> wallets;
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

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public Map<String, Wallet> getWallets() {
        return wallets;
    }

    public Totals getTotals() {
        return totals;
    }

    public Customer getCustomer() {
        return customer;
    }
}