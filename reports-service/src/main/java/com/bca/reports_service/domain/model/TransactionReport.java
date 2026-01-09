package com.bca.reports_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReport {
    private Card card;
    private List<Transaction> transactions;
    private Metadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        private String cardId;
        private String cardType;
        private String maskedNumber;
        private String currency;

        public String getCardId() {
            return cardId;
        }

        public String getCardType() {
            return cardType;
        }

        public String getMaskedNumber() {
            return maskedNumber;
        }

        public String getCurrency() {
            return currency;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transaction {
        private String transactionId;
        private String type;
        private Double amount;
        private String status;
        private LocalDateTime date;
        private String description;

        public String getTransactionId() {
            return transactionId;
        }

        public String getType() {
            return type;
        }

        public Double getAmount() {
            return amount;
        }

        public String getStatus() {
            return status;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public String getDescription() {
            return description;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        private Integer totalReturned;
        private Integer limit;

        public Integer getTotalReturned() {
            return totalReturned;
        }

        public Integer getLimit() {
            return limit;
        }
    }

    public Card getCard() {
        return card;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static TransactionReport empty() {
        return new TransactionReport(
                new Card(null, null, null, null),
                List.of(),
                new Metadata(0, 0)
        );
    }
}