package com.bca.wallets_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String fromWalletId;
    private String toWalletId;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceAfter;
    private TransactionStatus status;
    private LocalDateTime executedAt;

    public Transaction(String transactionId, String fromWalletId, String toWalletId, BigDecimal amount, String currency, BigDecimal balanceAfter, TransactionStatus status, LocalDateTime executedAt) {
        this.transactionId = transactionId;
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
        this.amount = amount;
        this.currency = currency;
        this.balanceAfter = balanceAfter;
        this.status = status;
        this.executedAt = executedAt;
    }

    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getFromWalletId() { return fromWalletId; }
    public void setFromWalletId(String fromWalletId) { this.fromWalletId = fromWalletId; }

    public String getToWalletId() { return toWalletId; }
    public void setToWalletId(String toWalletId) { this.toWalletId = toWalletId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public enum TransactionStatus {
        SUCCESS, FAILED
    }
}