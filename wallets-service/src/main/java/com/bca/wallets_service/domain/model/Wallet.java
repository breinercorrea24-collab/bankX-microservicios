package com.bca.wallets_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Wallet {
    protected String walletId;
    protected String customerId;
    protected BigDecimal balance;
    protected String currency;
    protected WalletStatus status;
    protected LocalDateTime createdAt;

    public Wallet(String walletId, String customerId, BigDecimal balance, String currency, WalletStatus status, LocalDateTime createdAt) {
        this.walletId = walletId;
        this.customerId = customerId;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public WalletStatus getStatus() { return status; }
    public void setStatus(WalletStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public enum WalletStatus {
        ACTIVE, INACTIVE
    }
}