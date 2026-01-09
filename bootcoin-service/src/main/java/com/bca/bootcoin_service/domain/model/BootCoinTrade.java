package com.bca.bootcoin_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootCoinTrade {
    private String tradeId;
    private TradeType tradeType;
    private BigDecimal amountBTC;
    private BigDecimal pricePEN;
    private BigDecimal totalAmount;
    private TradeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String buyerWalletId;
    private String sellerWalletId;

    public enum TradeType {
        BUY, SELL
    }

    public enum TradeStatus {
        OPEN, COMPLETED, CANCELLED
    }
}