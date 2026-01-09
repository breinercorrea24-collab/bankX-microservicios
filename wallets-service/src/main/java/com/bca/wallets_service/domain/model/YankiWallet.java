package com.bca.wallets_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class YankiWallet extends Wallet {
    private String phone;

    public YankiWallet(String walletId, String customerId, String phone, BigDecimal balance, String currency, WalletStatus status, LocalDateTime createdAt) {
        super(walletId, customerId, balance, currency, status, createdAt);
        this.phone = phone;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}