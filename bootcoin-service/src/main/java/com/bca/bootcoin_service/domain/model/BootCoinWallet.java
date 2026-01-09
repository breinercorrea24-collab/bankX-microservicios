package com.bca.bootcoin_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootCoinWallet {

    private String walletId;
    private String customerId;
    private String document;
    private BigDecimal balanceBTC;
    private WalletStatus status;
    private LocalDateTime createdAt;

    public enum WalletStatus {
        ACTIVE, INACTIVE, BLOCKED
    }
}